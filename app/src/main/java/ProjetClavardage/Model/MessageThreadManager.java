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

    private ArrayList<UserInConv> userInConvs;

    public MessageThreadManager() {
        this.userInConvs = new ArrayList<>();
    }

    public void OpenConnection() {
    }

    public void Send(Message msg) {
    }

    public void run(){
        int nb_conv_max = 50;
        int nb_connection = 0;
        Socket sock;
        Conversation[] conversations = new Conversation[50];
        for (int i = 0; i < nb_conv_max; i++) {
            conversations[i] = null;
        }

        ServerSocket servsock = null;
        try {
            servsock = new ServerSocket(9000,2, InetAddress.getLocalHost());

            while (nb_connection<=nb_conv_max) {
                sock = servsock.accept();
                int i = 0;
                while (conversations[i] != null) {  //On cherche le premier élément libre de conversations
                    i++;
                }
                conversations[i] = new Conversation("Coversation #" + i, servsock, sock);
                nb_connection++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
