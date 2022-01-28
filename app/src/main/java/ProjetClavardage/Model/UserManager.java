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

    /**
     * Constructeur
     * @param mc
     * @param privateUser Utilisateur associé au pc
     * @param listeningPort Port d'écoute UDP
     * @param sendingPort Port d'envoi (éventuellement différent du port d'écoute pour les tests en local)
     */
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

    /**
     * Renvoie l'utilisateur associé à l'ip rentrée
     * @param ip
     * @return
     */
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

    /**
     * Parcours les users déjà notés comme connectés lors du dernier appel :
     * Si l'utilisateur a été renoté connecté depuis le dernier appel, on le laisse affiché et on le note déconnecté
     * Si l'utilisateur est toujours déconnecté, on l'enlève du tableau et on le supprime de l'affichage
     */
    public void users_update () {
        synchronized (this.usersByIP) {
            try {
                this.semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (User user : usersByIP.values()
            ) {
                if (user.isConnected()) { // si il est connecté, on l'affiche et on réinitialise le tableau
                    this.mc.addUser(user);
                    this.mc.changeUserName(user, this.oldUsernamesByIp.get(user), user.getUsername());
                    user.setConnected(false);
                } else { // si il s'est deco
                    usersByIP.remove(user.getIP());
                    this.oldUsernamesByIp.remove(user);
                    this.mc.removeUser(user);
                }

            }
            semaphore.release();
        }
    }

    /**
     * Envoie une notification de connexion ou changement de pseudo à tous les autres utilisateurs (broadcast sur toutes les interfaces)
     * @param b true correspond a une connexion/changment de pseudo, false correspond a une deconnexion
     */
    public void sender(boolean b) {
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
    }

    /**
     * Lance le socket d'écoute UDP
     */
    public void start_listener() {
        try {
            this.dgramSocket = new DatagramSocket(this.listeningPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    /**
     * Ferme le socket d'écoute UDP
     */
    public void close_listener() {
        this.sender(false);
        this.dgramSocket.close();
    }

    /**
     * Ecoute les notification de connexion et changement de pseudo
     */
    public void run() {
            byte[] buffer = new byte[256];
            InetAddress lastAddress = null;
            while(true) {
                try {
                    DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                    dgramSocket.receive(inPacket);
                    InetAddress clientAddress = inPacket.getAddress();
                    synchronized (this.usersByIP) {
                        try {
                            this.semaphore.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (true) {
                            lastAddress = clientAddress;
                            int clientPort = inPacket.getPort();
                            String message = new String(inPacket.getData(), 0, inPacket.getLength());
                            if (!message.equals(privateUser.getUsername())) {
                                if (message.length() > 2) { //un message de moins de 3 caracteres correspond a une deconnexion
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
                                    this.usersByIP.get(clientAddress).setConnected(true);
                                } else { //cas d'une deconnexion
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


    @Deprecated
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
