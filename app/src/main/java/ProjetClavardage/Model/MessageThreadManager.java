package ProjetClavardage.Model;

import ProjetClavardage.Controller.MainController;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;

/*
TODO
    refactoring :
    remove composition from message to conv (have opposite)
    change comp from msg to user to association (done by adding user as param in constructor)
 */

/**
 * Classe gérant l'envoi et la reception de messages, et gère les conversations ainsi que la base de données
 */
public class MessageThreadManager extends Thread {

    public static final int NB_CONV_MAX = 50;
    public static final int NUM_PORT = 9000;

    private ArrayList<UserInConv> userInConvs;
    private ArrayList<Conversation> conversations;
    private int servPort;
    private int clientPort;

    private MainController mc;

    public MessageThreadManager(MainController mc, int servPort, int clientPort) {
        this.mc = mc;
        this.conversations = new ArrayList<>();
        this.userInConvs = new ArrayList<>();
        this.servPort = servPort;
        this.clientPort = clientPort;
    }

    // initie une connexion (d'envoi de message) du cote de l'utilisateur
    public Socket openConnection(InetAddress ipaddress, Conversation conv) {
        // TODO raise exception instead
        System.out.println("thdMngr [ENVOYEUR] openConnection sur ip=" + ipaddress);
        Socket sock = null;
        if (this.conversations.size()>=this.NB_CONV_MAX) {
            //Message erreur
            return sock;
        }
        try {
            System.out.println("thdMngr [ENVOYEUR] openConnection start socket creation");
            sock = new Socket(ipaddress, this.clientPort);
            System.out.println("thdMngr [ENVOYEUR] openConnection socket creation succes");
            conv.addUser(sock);

            //TODO vérifier si la conversation existe deja dans la bdd, si tel est le cas, on met l'uuid dans le constructeur
            if (!this.conversations.contains(conv)) {
                this.conversations.add(conv);
                System.out.println("here");
                SpecialMessage spemsg = new SpecialMessage(conv);
                conv.send_message(spemsg);

                if (conv.getUsersIP().size() > 0) {
                    System.out.println("demande conv envoyee a " + conv.getUsersIP().get(0));
                } else {
                    System.out.println("demande conv impossible a envoyer pas d'user dans conv");
                }
            } else {
                System.out.println("demande conv non envoyee car conv deja existante");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sock;
    }

    public void send(Message msg, int index) {
        this.conversations.get(index).send_message(msg);
    }

    public void run(){
        Socket sock;

        //ServerSocket servsock = null;

        //servsock = new ServerSocket(this.servPort,2, getLocalAdress());
        ServerSocket servsock = null;
        try {
            servsock = new ServerSocket(this.servPort, 0, getLocalAddress(MainController.ni));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*SocketAddress sa = new InetSocketAddress(this.servPort);*/
        /*try {
            servsock = new ServerSocket();
            servsock.bind(sa);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        while (this.conversations.size()<=this.NB_CONV_MAX) {
            try{
                System.out.println("thdMng [RECEVEUR] run : start servsock accept");
                sock = servsock.accept();
                System.out.println("thdMng [RECEVEUR] run : servsock accept succes");

                String str = "";
                ArrayList<InetAddress> usersIP = new ArrayList<>();
                usersIP.add(sock.getInetAddress());

                ObjectInputStream oiStream = new ObjectInputStream(sock.getInputStream());
                SpecialMessage msg = (SpecialMessage) oiStream.readObject();
                Conversation conv;

                System.out.println("special msg conv id=" + msg.getConvID());

                conv = DatabaseManager.getConversation(msg.getConvID(), this);

                if(this.conversations.contains(conv)) {
                    conv.addUser(sock);
                    System.out.println("devrait pas etre la");
                }
                else {
                    //System.out.println("remote address : " + sock.getRemoteSocketAddress().toString());
                    String username = DatabaseManager.getUser(((InetSocketAddress) sock.getRemoteSocketAddress()).getAddress()).getUsername();
                    conv = new Conversation(username, this,msg.getConvID());
                    conv.addUser(sock);
                    conv = this.mc.addConversationTab(conv);
                    this.conversations.add(conv);
                }
                int i = 0;
                for (InetAddress ip:msg.getUsersIP()  // Creation de connexion avec les autres users de la conv
                     ) {
                    System.out.println("i=" + i + ", ip=" + ip);
                    i++;
                    if (!ip.equals(this.mc.getPrivateUserIp())) {
                        openConnection(ip,conv);
                    }
                }

                System.out.println("demande conv recue : conv ip " + conv.getFirstIP().getHostAddress());

                this.mc.refreshUI();

                //TODO, ajouter les socket dans la conv
                //this.mc.addConversationTab(this.conversations.get(this.conversations.size() - 1).getName());

                // TODO : add username display to tab and contacts list (maybe use database relation with IP address?)
                //this.mc.addContact(sock.getInetAddress().toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public void close_conversation(int conv_id) {
        Conversation conv = this.conversations.get(conv_id);
        conv.close_connection();
        //this.conversations.get(conv_id).join();
        this.conversations.remove(conv_id);
        System.out.println("closed here 1");
    }

    public void close_conversation(InetAddress ip) {
        Conversation conv = this.getConversationByIP(ip);
        if (conv == null) {
            System.out.println("thdmngr [FERMEUR] conv null");
        } else {
            System.out.println("thdmngr [FERMEUR] conv not null");
        }
        conv.close_connection();
        this.conversations.remove(conv);
        System.out.println("closed here 2");
    }

    public void close_all_conversation() {
        for (Conversation conv: conversations
             ) {
            conv.close_connection();
        }
    }

    public void close_conversation_conv(Conversation conv) {
        conv.close_connection();
        if (this.conversations.indexOf(conv) != -1) {
            this.mc.removeConversationTab(conv);
        }
        this.conversations.remove(conv);
    }

    public Conversation getConversationsAt(int index) {
        return this.conversations.get(index);
    }

    public Conversation getConversationByIP(InetAddress ip) {
        for (Conversation conv :
                this.conversations) {
            if (conv.getUsersIP().size() > 0 && ip.equals(conv.getUsersIP().get(0))) {
                return conv;
            }
        }
        return null;
    }

    public void received(Message msg, Conversation conv) {
        String text = msg.getUser().getUsername() + ">" + msg.getContent();
        //this.mc.addTextToTab(this.conversations.indexOf(conv), text);
        this.mc.addTextToTab(conv,text);
    }
    public int getClientPort(){return this.clientPort;}

    public static InetAddress getLocalAddress() {
        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    InetAddress ia = (InetAddress) ias.nextElement();
                    if (!ia.isLoopbackAddress() &&
                    ia instanceof Inet4Address) {
                        return ia;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InetAddress getLocalAddress(String netInterface) {
        try {
            NetworkInterface ni = NetworkInterface.getByName(netInterface);
            Enumeration<InetAddress> ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                InetAddress ia = (InetAddress) ias.nextElement();
                if (!ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                    return ia;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Conversation getConvByID(UUID convId) {
        for (Conversation conv :
                this.conversations) {
            if (conv.getID().equals(convId)) {
                return conv;
            }
        }
        return null;
    }

}
