package ProjetClavardage.Model;

import ProjetClavardage.Controller.MainController;
import com.sun.tools.javac.Main;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

/*
TODO
    refactoring :
    remove two way composition between msgthdmngr and conv (how ?)
    remove composition from message to conv (have opposite) (
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
    public int openConnection(InetAddress IPaddress) {
        // TODO raise exception instead
        if (this.conversations.size()>=this.NB_CONV_MAX) {
            //Message erreur
            return -1;
        }
        try {
            System.out.println("Connect ip adress:" + IPaddress.toString());
            Socket sock = new Socket(IPaddress, this.clientPort);
            System.out.println("HERE conversation added");
            this.conversations.add(new Conversation("Conversation #" + this.conversations.size(), sock, this));
            this.conversations.get(this.conversations.size() - 1).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
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
            SocketAddress sa = new InetSocketAddress(getLocalAdress(), this.servPort);
            servsock = new ServerSocket();
            servsock.bind(sa);
            System.out.println("server listening on " + getLocalAdress() + " and port " + this.servPort);
            while (this.conversations.size()<=this.NB_CONV_MAX) {
                sock = servsock.accept();
                //int i = 0;
                /*while (this.conversations[i] != null) {  //On cherche le premier élément libre de conversations
                    i++;
                }*/
                //this.conversations[i] = new Conversation("Conversation #" + i, servsock, sock);
                this.conversations.add(new Conversation("Conversation #" + this.conversations.size(), sock, this));
                System.out.println("conversation added");
                this.conversations.get(this.conversations.size() - 1).start();
                this.mc.addConversationTab(InetAddress.getLocalHost().toString());
                // TODO : add username display to tab and contacts list (maybe use database relation with IP address?)
                this.mc.addContact(InetAddress.getLocalHost().toString());
            }
        } catch (IOException e) {
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
        this.mc.addTextToTab(this.conversations.indexOf(conv), msg.getContent());
    }

    private static InetAddress getLocalAdress() {
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
