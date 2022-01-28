package ProjetClavardage.Model;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Classe gérant les messages ainsi que l'ajout et la suppression de membres dans une conversation
 */
public class Conversation{                                                                                                                                                                                                                                                                   ;
    private ArrayList<Socket> socks;
    public static final int MSG_LENGTH = 280;
    private InputStream iStream;
    private OutputStream oStream;
    private MessageThreadManager msgThMng;
    private String name;
    private UUID id;
    private ArrayList<InetAddress> usersIP;
    private HashMap<InetAddress,UserInConv> usersHashMap;
    private List<User> users = new ArrayList<User> ();


    /**
     * Création d'une nouvelle conversation
     * @param str
     * @param msgThMng
     */
    public Conversation (String str, MessageThreadManager msgThMng) {
        this.name = str;
        this.socks = new ArrayList<>();
        this.msgThMng = msgThMng;
        this.id = UUID.randomUUID();
        this.usersIP = new ArrayList<>();
        this.usersHashMap = new HashMap<>();
    }

    /**
     * Ouverture d'une conversaton déjà existante
     * @param str
     * @param msgThMng
     * @param id
     */
    public Conversation (String str, MessageThreadManager msgThMng, UUID id) {
        this.name = str;
        this.socks = new ArrayList<>();
        this.msgThMng = msgThMng;
        this.id = id;
        this.usersIP = new ArrayList<>();
        this.usersHashMap = new HashMap<>();
    }

    @Deprecated
    public Conversation(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

    /**
     * Ferme la conversation
     */
    public void close_connection () {
        try {
            this.send_message(null);
            for (Socket sock :this.socks
                 ) {
                sock.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message
     * @param msg
     */
    public void send_message(Message msg) {
        for (InetAddress ip : this.usersIP
             ) {
            this.usersHashMap.get(ip).send_message(msg);
        }
    }

    /**
     * Ajoute un utilisateur à la conversation
     * @param sock socket de l'utilsateur à ajouter
     */
    public void addUser(Socket sock) {
        InetAddress ip = sock.getInetAddress();
        if (!usersIP.contains(ip)){
            this.usersIP.add(ip);
            UserInConv userinconv= new UserInConv("sock",sock,this.msgThMng, this);
            this.usersHashMap.put(ip,userinconv);
            this.usersHashMap.get(ip).start();
        }
    }
    @Deprecated
    public void maj_conv(SpecialMessage msg) {
    }

    public ArrayList<InetAddress> getUsersIP() {return this.usersIP;}

    /**
     * Retire l'utilisateur de la conversation
     * @param userIP ip de l'utilisateur à déconecter
     */
    public void removeUser(InetAddress userIP) {
        this.usersIP.remove(userIP);
        this.usersHashMap.get(userIP).close();
        this.usersHashMap.remove(userIP);
    }

    public UUID getID() {return this.id;}

    public String getConvName() {return  this.name;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(usersIP, that.usersIP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usersIP);
    }

    public String getName() {return this.name;}

    /**
     * Récupère l'IP de la première personne de la conversation
     * @return
     */
    public InetAddress getFirstIP() {
        if (this.usersIP.size() > 0) {
            return this.usersIP.get(0);
        }
        return null;
    }

    /**
     * Copie les utilisateurs de src à dest
     * @param src
     * @param dest
     */
    public static void copyUsers(Conversation src, Conversation dest) {
        dest.usersIP = src.usersIP;
        dest.usersHashMap = src.usersHashMap;
    }

}
