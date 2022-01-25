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
        synchronized (this) {
            for (User user : usersByIP.values()
            ) {
                if (user.isConnected()) { // si il est connecté, on l'affiche et on réinitialise le tableau
                    //TODO afficher user dans le panel
                    this.mc.addUser(user);
                    user.setConnected(false);
                    //System.out.println(user.getUsername() + "isConnected:" + user.isConnected());
                } else { // si il s'est deco
                    //System.out.println("user disconnected : " + user.getUsername());
                    usersByIP.remove(user.getIP());
                    this.mc.removeUser(user);
                }

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
                    if (networkInterface.isLoopback() || !networkInterface.isUp())
                        continue;   // Do not want to use the loopback interface.
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    // second condition for test only
                    if (broadcast == null || broadcast.toString().contains("10"))
                        continue;

                    DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                            message.length(),broadcast, this.sendingPort);
                    dgramSocket.send(outPacket);
                    //System.out.println("UDP sent message");
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
            byte[] buffer = new byte[256];
            InetAddress lastAddress = null;
            while(true) {
                try {
                    DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                    //System.out.println("UDP :"+"waiting..." );
                    dgramSocket.receive(inPacket);
                    InetAddress clientAddress = inPacket.getAddress();

                    // TODO regler pb detecter soi meme sur udp

                    if (true) {
                        //System.out.println("Received from " + clientAddress);
                        lastAddress = clientAddress;
                        int clientPort = inPacket.getPort();
                        String message = new String(inPacket.getData(), 0, inPacket.getLength());
                        //System.out.println("UDP :"+message );
                        if (!message.equals(privateUser.getUsername())) {
                            System.out.println("nom recu : "+message+"    nom de l'user : " + privateUser.getUsername());
                            if (message.length()>2) {
                                //System.out.println("entered in if");//un message de moins de 3 caracteres correspond a une deconnexion
                                if (this.usersByIP.containsKey(clientAddress)){ //cas ou l'utilisateur est déja connu
                                    this.usersByIP.get(clientAddress).setUsername(message);
                                }
                                else {  //cas ou on découvre qu'il est connecte
                                    User user = new User(clientAddress,clientPort,message);
                                    this.usersByIP.put(clientAddress,user);
                                }
                                //System.out.println("Info : "+ clientAddress + "is still connected");
                                this.usersByIP.get(clientAddress).setConnected(true);
                                String response= privateUser.getUsername();
                                DatagramPacket outPacket = new DatagramPacket(response.getBytes(), response.length(),
                                        clientAddress, clientPort);
                                dgramSocket.send(outPacket);
                            }
                            else{ //cas d'une deconnexion
                                //System.out.println("deco");
                                if (this.usersByIP.containsKey(clientAddress)){
                                    this.mc.removeUser(usersByIP.get(clientAddress));
                                    this.usersByIP.remove(clientAddress);
                                }
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    // ?
    public void notifyConnected() {
    }

    // ?
    public boolean isConnected(InetAddress userIP) {
        return this.usersByIP.get(userIP).isConnected();
    }

}
