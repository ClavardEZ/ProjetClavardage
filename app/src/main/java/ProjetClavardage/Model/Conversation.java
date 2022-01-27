package ProjetClavardage.Model;

import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Conversation{
    //private byte[] err =                                                                                                                                                                                                                                                                                        ;
    private ArrayList<Socket> socks;
    public static final int MSG_LENGTH = 280;
    private InputStream iStream;
    private OutputStream oStream;
    private MessageThreadManager msgThMng;
    private String name;
    private UUID id;
    private ArrayList<InetAddress> usersIP;
    private HashMap<InetAddress,UserInConv> usersHashMap;



    /*private ObjectInputStream oiStream;
    private ObjectOutputStream ooStream;*/

    public Conversation (String str, MessageThreadManager msgThMng) {
        this.name = str;
        this.socks = new ArrayList<>();
        this.msgThMng = msgThMng;
        this.id = UUID.randomUUID();
        this.usersIP = new ArrayList<>();
        this.usersHashMap = new HashMap<>();
    }

    public Conversation (String str, MessageThreadManager msgThMng, UUID id) {
        this.name = str;
        this.socks = new ArrayList<>();
        this.msgThMng = msgThMng;
        this.id = id;
        this.usersIP = new ArrayList<>();
        this.usersHashMap = new HashMap<>();
    }


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

    public void send_message(Message msg) {
        for (InetAddress ip : this.usersIP
             ) {
            this.usersHashMap.get(ip).send_message(msg);
        }
    }

    public void init(){

        //Je crois que ca sert a rien
        /*
        for (Socket sock: this.socks)
            {
                UserInConv userinconv= new UserInConv("sock",sock,this.msgThMng, this);
                this.usersHashMap.put(sock.getInetAddress(),userinconv);
            }*/
    }
    public void start() {
        //Je crois que ca sert a rien
        /*
        for (InetAddress ip: usersIP
             ) {
            this.usersHashMap.get(ip).start();
        }*/
    }

    private List<User> users = new ArrayList<User> ();


    public Conversation(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

    public void addUser(Socket sock) {
        InetAddress ip = sock.getInetAddress();
        if (!usersIP.contains(ip)){
            this.usersIP.add(ip);
            UserInConv userinconv= new UserInConv("sock",sock,this.msgThMng, this);
            this.usersHashMap.put(ip,userinconv);
            this.usersHashMap.get(ip).start();
        }
    }
    public void maj_conv(SpecialMessage msg) {
        //TODO faire cette classe de mort
        /*
        for (InetAddress ip: ((SpecialMessage) msg).getUsersIP()
        ) {
            if (!this.usersIP.contains(ip)) {
                this.addUser(ip);
            }
        }
        for (InetAddress ip: this.getUsersIP()
        ) {
            if (!((SpecialMessage) msg).getUsersIP().contains(ip)) {
                this.removeUser(ip);
            }
        }*/
    }

    public ArrayList<InetAddress> getUsersIP() {return this.usersIP;}

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

}
