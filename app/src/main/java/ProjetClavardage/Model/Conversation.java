package ProjetClavardage.Model;

import java.util.UUID;
import java.util.Date;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;

public class Conversation extends Thread {
    //private byte[] err =                                                                                                                                                                                                                                                                                        ;
    private ArrayList<Socket> socks;
    public static final int MSG_LENGTH = 280;
    private InputStream iStream;
    private OutputStream oStream;
    private MessageThreadManager msgThMng;
    private String name;
    private UUID id;
    private ArrayList<InetAddress> usersIP;


    /*private ObjectInputStream oiStream;
    private ObjectOutputStream ooStream;*/

    public Conversation (String str, ArrayList<Socket> s, MessageThreadManager msgThMng,ArrayList<InetAddress> usersIP) {
        super(str);
        this.name = str;
        this.socks = s;
        this.msgThMng = msgThMng;
        this.id = UUID.randomUUID();
        System.out.println("IDCONV : "+this.id);
        this.usersIP = usersIP;
    }

    public Conversation (String str, ArrayList<Socket> s, MessageThreadManager msgThMng, UUID id) {
        super(str);
        this.name = str;
        this.socks = s;
        this.msgThMng = msgThMng;
        this.id = id;
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
        try {
            //byte[] data = new byte[280];
            //data = msg.getContent().getBytes(StandardCharsets.UTF_8);
            //this.oStream.write(data);
            //System.out.println("conv sent message");
            for (Socket sock: this.socks
                 ) {
                ObjectOutputStream ooStream = new ObjectOutputStream(sock.getOutputStream());
                ooStream.writeObject(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        /*int ret = 0;
        try {
            // TODO not use exception, maybe send objects instead of Strings/bytes ?
            while (ret != -1) {
                byte[] data = new byte[MSG_LENGTH];
                //System.out.println("run conv ip: "+InetAddress.getLocalHost().toString());
                ret = this.iStream.read(data);
                String received_msg = new String(data, StandardCharsets.UTF_8).trim();
                Date date = new Date();
                Message message = new TextMessage(date, this, received_msg);
                //System.out.println("received byte : " + data.toString());
                //System.out.println("received : " + received_msg);
                this.msgThMng.received(message, this);
                //System.out.println("sock connected : " + this.sock.isConnected());
            }
            System.out.println("socket closed");
            this.msgThMng.close_conversation_conv(this);
        } catch (SocketException se) {
            System.out.println("socket exception closed");
        } catch (IOException el) {
            el.printStackTrace();
        }*/

        try {
            Message msg = null;
            ObjectInputStream oiStream = null;
            do {
                for (Socket sock: this.socks
                     ) {
                    oiStream = new ObjectInputStream(sock.getInputStream());
                    msg = (Message) oiStream.readObject();
                    if (msg instanceof SpecialMessage){  // un utilisateur ajoute et/ou supprime des gens -> on mets a jour usersID
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
                        }
                    }
                    if (msg != null) {
                        System.out.println("received msg=" + msg.toString());
                        this.msgThMng.received(msg, this);
                    }
                }
            } while (msg != null);
            System.out.println("closed");
            this.msgThMng.close_conversation_conv(this);
        } catch (SocketException e) {
            System.out.println("Client disconnected");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private List<User> users = new ArrayList<User> ();

    public Conversation(List<User> users) {
    }

    public Conversation(String name) {
        this.name = name;
        this.id = UUID.randomUUID();
    }

    public void addUser(InetAddress userIP) {
        if (!usersIP.contains(userIP)){
            this.usersIP.add(userIP);
        }

    }

    public ArrayList<InetAddress> getUsersIP() {return this.usersIP;}

    public void removeUser(InetAddress userIP) {
        this.usersIP.remove(userIP);
    }

    public UUID getID() {return this.id;}

    public String getConvName() {return  this.name;}



}
