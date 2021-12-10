package ProjetClavardage.Model;

import java.util.UUID;
import java.util.Date;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Conversation extends Thread {
    //private byte[] err =                                                                                                                                                                                                                                                                                        ;
    private Socket sock;
    public static final int MSG_LENGTH = 280;
    private InputStream iStream;
    private OutputStream oStream;
    private MessageThreadManager msgThMng;
    private String name;
    private UUID id;

    /*private ObjectInputStream oiStream;
    private ObjectOutputStream ooStream;*/

    public Conversation (String str, Socket s, MessageThreadManager msgThMng) {
        super(str);
        this.name = str;
        this.sock = s;
        this.msgThMng = msgThMng;
        this.id = UUID.randomUUID();
        try {
            this.iStream = this.sock.getInputStream();
            this.oStream = this.sock.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Conversation (String str, Socket s, MessageThreadManager msgThMng, UUID id) {
        super(str);
        this.name = str;
        this.sock = s;
        this.msgThMng = msgThMng;
        this.id = id;
        try {
            this.iStream = this.sock.getInputStream();
            this.oStream = this.sock.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close_connection () {
        try {
            this.send_message(null);
            this.sock.close();
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
            ObjectOutputStream ooStream = new ObjectOutputStream(this.oStream);
            ooStream.writeObject(msg);
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
                oiStream = new ObjectInputStream(this.iStream);
                msg = (Message) oiStream.readObject();
                if (msg != null) {
                    System.out.println("received msg=" + msg.toString());
                    this.msgThMng.received(msg, this);
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

    public Conversation() {
    }

    public void addUser(User user) {
    }

    public void removeUser(User user) {
    }

    public UUID getID() {return this.id;}



}
