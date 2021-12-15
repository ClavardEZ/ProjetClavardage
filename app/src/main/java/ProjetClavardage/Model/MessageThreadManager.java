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
    private HashMap<UUID,Conversation> conversationHashMap;
    private int servPort;
    private int clientPort;

    private MainController mc;

    public MessageThreadManager(MainController mc, int servPort, int clientPort) {
        this.mc = mc;
        this.conversations = new ArrayList<>();
        this.conversationHashMap = new HashMap<>();
        this.userInConvs = new ArrayList<>();
        this.servPort = servPort;
        this.clientPort = clientPort;
    }

    // initie une connexion (d'envoi de message) du cote de l'utilisateur
    public Socket openConnection(InetAddress ipaddress, String username, Conversation conv) {
        // TODO raise exception instead
        Socket sock = null;
        if (this.conversations.size()>=this.NB_CONV_MAX) {
            //Message erreur
            return sock;
        }
        try {
            System.out.println("Connect ip adress:" + ipaddress.toString());

            sock = new Socket(ipaddress, this.clientPort);
            conv.addUser(sock);
            System.out.println("HERE conversation added");

            //TODO vérifier si la conversation existe deja dans la bdd, si tel est le cas, on met l'uuid dans le constructeur
            this.conversationHashMap.put(conv.getID(),conv);
            this.conversations.add(conv);
            //conv.init();
            //conv.start();
            SpecialMessage spemsg = new SpecialMessage(conv);
            System.out.println("UUID sent : "+ spemsg.getConvID());
            conv.send_message(spemsg);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sock;
    }

    public void send(Message msg, int index) {
        System.out.println("msg manager sent message");
        this.conversations.get(index).send_message(msg);
    }

    public void run(){
        Socket sock;

        ServerSocket servsock = null;

        try {
            //servsock = new ServerSocket(this.servPort,2, getLocalAdress());
            SocketAddress sa = new InetSocketAddress(getLocalAddress(), this.servPort);
            servsock = new ServerSocket();
            servsock.bind(sa);
            System.out.println("server listening on " + getLocalAddress() + " and port " + this.servPort);
            while (this.conversations.size()<=this.NB_CONV_MAX) {
                sock = servsock.accept();
                //int i = 0;
                /*while (this.conversations[i] != null) {  //On cherche le premier élément libre de conversations
                    i++;
                }*/
                //this.conversations[i] = new Conversation("Conversation #" + i, servsock, sock);
                String str = "";
                ArrayList<InetAddress> usersIP = new ArrayList<>();
                usersIP.add(sock.getInetAddress());


                ObjectInputStream oiStream = new ObjectInputStream(sock.getInputStream());
                SpecialMessage msg = (SpecialMessage) oiStream.readObject();

                System.out.println("UUID received : "+ msg.getConvID());
                Conversation conv;
                if(conversationHashMap.containsKey(msg.getConvID())) {
                    conv = conversationHashMap.get(msg.getConvId());
                    conv.addUser(sock);
                }
                else {
                    conv = new Conversation("osef", this,msg.getConvID());
                    this.conversations.add(conv);
                    conv.addUser(sock);
                    System.out.println("conversation added");
                }
                for (InetAddress ip:msg.getUsersIP()  // Creation de connexion avec les autres users de la conv
                     ) {
                    openConnection(ip,"osef",conv);
                }

                //TODO, ajouter les socket dans la conv
                this.mc.addConversationTab(this.conversations.get(this.conversations.size() - 1).getName());
                // TODO : add username display to tab and contacts list (maybe use database relation with IP address?)
                //this.mc.addContact(sock.getInetAddress().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void close_conversation(int conv_id) {
        this.conversations.get(conv_id).close_connection();
        System.out.println("conv closed");
        //this.conversations.get(conv_id).join();
        this.conversations.remove(conv_id);
        System.out.println("conv removed");
    }

    public void close_conversation_conv(Conversation conv) {
        System.out.println("conv closed from distant");
        conv.close_connection();
        if (this.conversations.indexOf(conv) != -1) {
            System.out.println("removed from pan");
            this.mc.removeConversationTab(this.conversations.indexOf(conv));
        }
        this.conversations.remove(conv);
    }

    public Conversation getConversationsAt(int index) {
        return this.conversations.get(index);
    }

    public void received(Message msg, Conversation conv) {
        String text = msg.getUser().getUsername() + ">" + msg.getContent();
        this.mc.addTextToTab(this.conversations.indexOf(conv), text);
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

}
