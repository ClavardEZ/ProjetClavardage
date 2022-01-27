package ProjetClavardage.Model;
import ProjetClavardage.Controller.MainController;
import com.sun.tools.javac.Main;

import java.io.IOException;
import java.util.ArrayList;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 * Classe qui gère les utilisateurs et leur connexions
 */
public class UserManager extends Thread {
    private PrivateUser privateUser;
    private int listeningPort;
    private int sendingPort;
    private HashMap<InetAddress, User> usersByIP;
    private HashMap<User, String> oldUsernamesByIp;
    DatagramSocket dgramSocket;
    private Semaphore semaphore;

    private MainController mc;

    public UserManager(MainController mc, PrivateUser privateUser, int listeningPort, int sendingPort) {
        this.mc = mc;
        this.privateUser = privateUser;
        this.listeningPort = listeningPort;
        this.sendingPort = sendingPort;
        this.usersByIP = new HashMap<>();
        this.oldUsernamesByIp = new HashMap<>();
        this.semaphore = new Semaphore(1);

        UserSender sender = new UserSender(this);
        sender.start();
    }

    public User getUserByIP(InetAddress ip) {
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user =  this.usersByIP.get(ip);
        semaphore.release();
        return user;
    }

    public void users_update () {
        synchronized (this) {
            try {
                this.semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (User user : usersByIP.values()
            ) {
                if (user.isConnected()) { // si il est connecté, on l'affiche et on réinitialise le tableau
                    //TODO afficher user dans le panel
                    this.mc.addUser(user);
                    this.mc.changeUserName(user, this.oldUsernamesByIp.get(user), user.getUsername());
                    user.setConnected(false);
                    //System.out.println(user.getUsername() + "isConnected:" + user.isConnected());
                } else { // si il s'est deco
                    //System.out.println("user disconnected : " + user.getUsername());
                    usersByIP.remove(user.getIP());
                    this.oldUsernamesByIp.remove(user);
                    this.mc.removeUser(user);
                }

            }
            semaphore.release();
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
                    if (broadcast == null)
                        continue;

                    DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                            message.length(),broadcast, this.sendingPort);
                    dgramSocket.send(outPacket);
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
                    synchronized (this) {

                        try {
                            this.semaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (true) {
                            //System.out.println("Received from " + clientAddress);
                            lastAddress = clientAddress;
                            int clientPort = inPacket.getPort();
                            String message = new String(inPacket.getData(), 0, inPacket.getLength());
                            //System.out.println("UDP :"+message );
                            if (!message.equals(privateUser.getUsername())) {
                                if (message.length() > 2) {
                                    //System.out.println("entered in if");//un message de moins de 3 caracteres correspond a une deconnexion
                                    if (this.usersByIP.containsKey(clientAddress)) { //cas ou l'utilisateur est déja connu
                                        this.usersByIP.get(clientAddress).setUsername(message);
                                    } else {  //cas ou on découvre qu'il est connecte
                                        User user = new User(clientAddress, clientPort, message);
                                        this.usersByIP.put(clientAddress, user);
                                        this.oldUsernamesByIp.put(user, user.getUsername());
                                        String response = privateUser.getUsername();
                                        DatagramPacket outPacket = new DatagramPacket(response.getBytes(), response.length(),
                                                clientAddress, clientPort);
                                        dgramSocket.send(outPacket);
                                    }
                                    //System.out.println("Info : "+ clientAddress + "is still connected");
                                    this.usersByIP.get(clientAddress).setConnected(true);
                                } else { //cas d'une deconnexion
                                    //System.out.println("deco");
                                    if (this.usersByIP.containsKey(clientAddress)) {
                                        this.mc.removeUser(usersByIP.get(clientAddress));
                                        this.oldUsernamesByIp.remove(this.usersByIP.get(clientAddress));
                                        this.usersByIP.remove(clientAddress);
                                    }
                                }
                            }
                        }
                    }
                    semaphore.release();
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
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean ret = this.usersByIP.get(userIP).isConnected();
        this.semaphore.release();
        return ret;
    }

}
