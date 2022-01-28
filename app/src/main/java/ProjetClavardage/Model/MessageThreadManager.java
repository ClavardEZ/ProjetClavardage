package ProjetClavardage.Model;

import ProjetClavardage.Controller.MainController;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.UUID;


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

    /**
     *
     * @param mc MainController de l'application
     * @param servPort Numéro de port d'écoute
     * @param clientPort Numéro de port d'envoi (éventuellement différent de celui d'écoute pour les tests en local)
     */
    public MessageThreadManager(MainController mc, int servPort, int clientPort) {
        this.mc = mc;
        this.conversations = new ArrayList<>();
        this.userInConvs = new ArrayList<>();
        this.servPort = servPort;
        this.clientPort = clientPort;
    }

    /**
     * Initie une connexion (d'envoi de message) du cote de l'utilisateur en créant un socket et en partageant avec l'hôte distant les informations de la conversation
     * @param ipaddress Ip de l'hote distant
     * @param conv Conversation associée à cette nouvelle connexion
     * @return renvoie le socket créé
     */
    public Socket openConnection(InetAddress ipaddress, Conversation conv) {
        Socket sock = null;
        if (this.conversations.size()>=this.NB_CONV_MAX) {
            return sock;
        }
        try {
            sock = new Socket(ipaddress, this.clientPort);
            conv.addUser(sock);
            if (!this.conversations.contains(conv)) {
                this.conversations.add(conv);
                SpecialMessage spemsg = new SpecialMessage(conv);
                conv.send_message(spemsg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sock;
    }

    /**
     * Envoie un message aux membres de la conversation
     * @param msg Message a envoyer
     * @param index index de la conversation
     */
    public void send(Message msg, int index) {
        this.conversations.get(index).send_message(msg);
    }

    /**
     * Thread gérant la réception des messages d'ouverture de connexion
     */
    public void run(){
        Socket sock;
        ServerSocket servsock = null;
        try {
            servsock = new ServerSocket(this.servPort, 0, getLocalAddress(this.mc.getNi()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (this.conversations.size()<=this.NB_CONV_MAX) {
            try{
                sock = servsock.accept();

                String str = "";
                ArrayList<InetAddress> usersIP = new ArrayList<>();
                usersIP.add(sock.getInetAddress());

                ObjectInputStream oiStream = new ObjectInputStream(sock.getInputStream());
                SpecialMessage msg = (SpecialMessage) oiStream.readObject();
                Conversation conv;


                conv = DatabaseManager.getConversation(msg.getConvID(), this);

                if(this.conversations.contains(conv)) {
                    conv.addUser(sock);
                }
                else {
                    String username = DatabaseManager.getUser(((InetSocketAddress) sock.getRemoteSocketAddress()).getAddress()).getUsername();
                    conv = new Conversation(username, this,msg.getConvID());
                    conv.addUser(sock);
                    conv = this.mc.addConversationTab(conv);
                    this.conversations.add(conv);
                }
                int i = 0;
                for (InetAddress ip:msg.getUsersIP()  // Creation de connexion avec les autres users de la conv
                     ) {
                    i++;
                    if (!ip.equals(this.mc.getPrivateUserIp())) {
                        openConnection(ip,conv);
                    }
                }

                this.mc.refreshUI();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Ferme une conversation
     * @param conv_id ID de la conversation à fermer
     */
    public void close_conversation(int conv_id) {
        Conversation conv = this.conversations.get(conv_id);
        conv.close_connection();
        //this.conversations.get(conv_id).join();
        this.conversations.remove(conv_id);
    }

    /**
     * Ferme une conversation
     * @param ip IP liée a la conversation à fermer
     */
    public void close_conversation(InetAddress ip) {
        Conversation conv = this.getConversationByIP(ip);
        conv.close_connection();
        this.conversations.remove(conv);
    }

    /**
     * Ferme toutes le conversations
     */
    public void close_all_conversation() {
        for (Conversation conv: conversations
             ) {
            conv.close_connection();
        }
    }

    /**
     * ferme une conversation
     * @param conv conversation à fermer
     */
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

    /**
     * Notifie le controller de la reception d'un message
     * @param msg message
     * @param conv conversation associée
     */
    public void received(Message msg, Conversation conv) {
        String text = msg.getUser().getUsername() + ">" + msg.getContent();
        this.mc.addTextToTab(conv,text);
    }
    public int getClientPort(){return this.clientPort;}

    @Deprecated
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

    /**
     * Retourne l'adresse correspondant à l'interface donnée
     * @param netInterface interface
     * @return adresse associée
     */
    public static InetAddress getLocalAddress(String netInterface) {
        try {
            NetworkInterface ni = NetworkInterface.getByName(netInterface);
            Enumeration<InetAddress> ias = ni.getInetAddresses();
            while (ias.hasMoreElements()) {
                InetAddress ia = (InetAddress) ias.nextElement();
                if (ia instanceof Inet4Address) {
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
