package ProjetClavardage.Model;
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

    private ArrayList<UserInConv> userInConvs;
    private ArrayList<Conversation> conversations;

    public MessageThreadManager() {
        this.conversations = new ArrayList<>();
        this.userInConvs = new ArrayList<>();
        /*for (int i = 0; i < NB_CONV_MAX; i++) {
            this.conversations = null;
        }*/
    }

    // initie une connexion (d'nvoi de message) du cote de l'utilisateur
    public int openConnection(InetAddress IPaddress) {
        if (this.conversations.size()==this.NB_CONV_MAX) {
            //Message erreur
            return -1;
        }
        try {
            Socket sock = new Socket(IPaddress, NUM_PORT);
            this.conversations.add(new Conversation("Conversation #" + this.conversations.size(), sock));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public void Send(Message msg) {
    }

    public void run(){
        Socket sock;

        ServerSocket servsock = null;
        try {
            servsock = new ServerSocket(NUM_PORT,2, InetAddress.getLocalHost());

            while (this.conversations.size()<=this.NB_CONV_MAX) {
                sock = servsock.accept();
                //int i = 0;
                /*while (this.conversations[i] != null) {  //On cherche le premier élément libre de conversations
                    i++;
                }*/
                //this.conversations[i] = new Conversation("Conversation #" + i, servsock, sock);
                this.conversations.add(new Conversation("Conversation #" + this.conversations.size(), sock));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close(int conv_id) {}
}
