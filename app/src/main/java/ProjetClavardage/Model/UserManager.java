package ProjetClavardage.Model;
import ProjetClavardage.Controller.MainController;

import java.io.IOException;
import java.util.ArrayList;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Classe qui gère les utilisateurs et leur connexions
 */
public class UserManager extends Thread {
    private PrivateUser privateUser;
    private int listeningPort;
    private int sendingPort;
    private HashMap<InetAddress, User> usersByIP;
    DatagramSocket dgramSocket;

    private MainController mc;

    public UserManager(MainController mc, PrivateUser privateUser, int listeningPort, int sendingPort) {
        this.mc = mc;
        this.privateUser = privateUser;
        this.listeningPort = listeningPort;
        this.sendingPort = sendingPort;
        this.usersByIP = new HashMap<>();

        UserSender sender = new UserSender(this);
        sender.start();
    }

    public User getUserByIP(InetAddress ip) {
        return this.usersByIP.get(ip);
    }

    public void users_update () {

        for (User user:usersByIP.values()
             ) {
            if (user.isConnected()) { // si il est connecté, on l'affiche et on réinitialise le tableau
                //TODO afficher user dans le pannel
                this.mc.addUser(user);
                user.setConnected(false);
                System.out.println(user.getUsername() + "isConnected:" + user.isConnected());
            }
            else { // si
                System.out.println("user disconnected : " + user.getUsername());
                usersByIP.remove(user.getIP());
                this.mc.removeUser(user);
            }

        }
    }


    // notifie aux autres utilisateurs que l'utilisateur courant est connecte
    // et envoie ses informations pour etre contacte
    public void selfConnected() {
    }

    public void sender(boolean b) { // b==true correspond a une connexion/changment de pseudo, b==false correspond a une deconnexion
        String message = "XX";
        if (b) {
            message = this.privateUser.getUsername();
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                    if (networkInterface.isLoopback())
                        continue;    // Do not want to use the loopback interface.
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    // second condition for test only
                    if (broadcast == null || broadcast.toString().contains("10"))
                        continue;

                    DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                            message.length(),broadcast, this.sendingPort);
                    dgramSocket.send(outPacket);
                    System.out.println("UDP sent message");
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                    message.length(),InetAddress.getLocalHost(), NUM_PORT);
            dgramSocket.send(outPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                message.length(),host, port);*/
    }

    public void start_listener() {
        try {
            this.dgramSocket = new DatagramSocket(this.listeningPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public void close_listener() {
        this.sender(false);
        this.dgramSocket.close();
    }

    // ecoute les connexion entrantes de nouveaux utilisateurs
    public void run() {
        try {
            byte[] buffer = new byte[256];
            InetAddress lastAddress = null;
            while(true) {
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                System.out.println("UDP :"+"waiting..." );
                dgramSocket.receive(inPacket);
                InetAddress clientAddress = inPacket.getAddress();
                if (clientAddress == lastAddress) {
                    continue;
                }
                lastAddress = clientAddress;
                int clientPort = inPacket.getPort();
                String message = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println("UDP :"+message );
                if (message.length()>2) { //un message de moins de 3 caractères correspond à une deconnexion

                    User user = new User(clientAddress,clientPort,message);
                    if (message!=user.getUsername()) {user.setUsername(message);}
                }
                else{
                    connected_users.remove(new User(clientAddress,clientPort,message));
                }
                String response= privateUser.getUsername();
                DatagramPacket outPacket = new DatagramPacket(response.getBytes(), response.length(),
                                                                clientAddress, clientPort);
                dgramSocket.send(outPacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ?
    public void notifyConnected() {
    }

    // ?
    public boolean isConnected(User user) {
        return this.connected_users.contains(user);
    }

}
