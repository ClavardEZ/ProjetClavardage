package ProjetClavardage.Model;

import java.io.*;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.*;

/**
 * Classe qu gère les sockets avec les utilisateurs distants au sein d'une conversation
 */
public class UserInConv extends Thread{
    Socket sock;
    MessageThreadManager msgThMng;
    InputStream iStream;
    OutputStream oStream;
    Conversation conv;

    /**
     *
     * @param str nom du thread
     * @param sock socket associé a l'utilisateur distant
     * @param msgThMng MessageThreadManager de l'application
     * @param conv conversation à laquelle est associé le UserInConv
     */
    public UserInConv(String str, Socket sock, MessageThreadManager msgThMng, Conversation conv){
        super(str);
        this.sock = sock;
        this.msgThMng= msgThMng;
        this.conv = conv;
        try {
            this.iStream = this.sock.getInputStream();
            this.oStream = this.sock.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie le message à l'utilisateur distant
     * @param msg message à envoyer
     */
    public void send_message(Message msg) {
        try {
            ObjectOutputStream ooStream = new ObjectOutputStream(sock.getOutputStream());
            ooStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread gérant la réception des messages de l'utilisateur distant
     */
    public void run() {
        try {
        Message msg = null;
        ObjectInputStream oiStream = null;
        do {
            oiStream = new ObjectInputStream(this.iStream);
            msg = (Message) oiStream.readObject();

            if (msg instanceof SpecialMessage){  // un utilisateur ajoute et/ou supprime des gens -> on met a jour usersID
                this.conv.maj_conv((SpecialMessage) msg);
            }

            else if (msg != null) {
                this.msgThMng.received(msg, msgThMng.getConvByID(msg.getConvID()));
                DatabaseManager.addMessage(msg);
            }
        } while (msg != null);
        this.msgThMng.close_conversation_conv(this.conv);
        } catch (SocketException e) {
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renvoie l'IP associé à l'utilisateur distant
     * @return
     */
    public InetAddress getSocketAddress() {
        return this.sock.getInetAddress();
    }
}
