package ProjetClavardage.Model;
import ProjetClavardage.View.Pan;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe gérant l'envoi et la reception de messages, et gère les conversations ainsi que la base de données
 */
public class MessageThreadManager extends Thread {

    public static final int NB_CONV_MAX = 50;
    public static final int NUM_PORT = 9000;
    // temporary for tests
    public static final int NUM_PORT_ECOUTE = 9001;
    public static final int NUM_PORT_ENVOI = 9000;

    private ArrayList<UserInConv> userInConvs;
    private ArrayList<Conversation> conversations;
    private Pan panel;

    public MessageThreadManager(Pan panel) {
        this.panel = panel;
        this.conversations = new ArrayList<>();
        this.userInConvs = new ArrayList<>();
    }

    // initie une connexion (d'envoi de message) du cote de l'utilisateur
    public int openConnection(InetAddress IPaddress) {
        if (this.conversations.size()==this.NB_CONV_MAX) {
            //Message erreur
            return -1;
        }
        try {
            Socket sock = new Socket(IPaddress, NUM_PORT_ENVOI);
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
            servsock = new ServerSocket(NUM_PORT_ECOUTE,2, InetAddress.getLocalHost());

            while (this.conversations.size()<=this.NB_CONV_MAX) {
                sock = servsock.accept();
                //int i = 0;
                /*while (this.conversations[i] != null) {  //On cherche le premier élément libre de conversations
                    i++;
                }*/
                //this.conversations[i] = new Conversation("Conversation #" + i, servsock, sock);
                this.conversations.add(new Conversation("Conversation #" + this.conversations.size(), sock, this));
                this.conversations.get(this.conversations.size() - 1).start();
                this.panel.addConversationTab(InetAddress.getLocalHost().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close_conversation(int conv_id) {
        try {
            this.conversations.get(conv_id).close_connection();
            this.conversations.get(conv_id).join();
            this.conversations.remove(conv_id);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close_conversation_conv(Conversation conv) {
        this.conversations.remove(conv);
        conv.close_connection();
        this.panel.closeConversation(this.conversations.indexOf(conv));
    }

    public Conversation getConversationsAt(int index) {
        return this.conversations.get(index);
    }

    public void received(Message msg, Conversation conv) {
        this.panel.addTextToTab(this.conversations.indexOf(conv), msg.getContent());
    }
}
