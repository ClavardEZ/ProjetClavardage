package ProjetClavardage.Model;

import java.util.Date;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Conversation extends Thread{
    //private byte[] err =                                                                                                                                                                                                                                                                                        ;
    private Socket sock;
    public static final int MSG_LENGTH = 280;
    private InputStream iStream;
    private OutputStream oStream;
    private MessageThreadManager msgThMng;

    public Conversation (String str, Socket s, MessageThreadManager msgThMng) {
        super(str);
        sock = s;
        this.msgThMng = msgThMng;
        try {
            this.iStream = sock.getInputStream();
            this.oStream = sock.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close_connection () {
        try {
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send_message(Message msg) {
        try {

            byte[] data = new byte[280];
            data = msg.getContent().getBytes(StandardCharsets.UTF_8);

            this.oStream.write(data);
            System.out.println("conv sent message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        int ret = 0;
        try {
            while (ret != -1) {
                byte[] data = new byte[MSG_LENGTH];
                //System.out.println("run conv ip: "+InetAddress.getLocalHost().toString());
                ret = this.iStream.read(data);
                String received_msg = new String(data, StandardCharsets.UTF_8).trim();
                Date date = new Date();
                Message message = new TextMessage(date, this, received_msg);
                System.out.println("received byte : " + data.toString());
                System.out.println("received : " + received_msg);
                this.msgThMng.received(message, this);
                System.out.println("sock connected : " + this.sock.isConnected());
            }
            System.out.println("socket closed");
            this.msgThMng.close_conversation_conv(this);
        } catch (SocketException se) {
            System.out.println("socket exception closed");
        } catch (IOException el) {
            el.printStackTrace();
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

}
