package ProjetClavardage.Model;

import java.util.Date;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Conversation extends Thread{
    private byte[] err =                                                                                                                                                                                                                                                                                        ;
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
        try {
            while (!this.sock.isConnected()) {
                byte[] data = new byte[MSG_LENGTH];
                System.out.println("run conv ip: "+InetAddress.getLocalHost().toString());
                this.iStream.read(data);
                String received_msg = new String(data);
                Date date = new Date();
                // remontee du msg recu
                Message message = new TextMessage(date, this, received_msg);
                System.out.println("received : " + received_msg);
                this.msgThMng.received(message, this);
            }
            System.out.println("socket closed");
            this.msgThMng.close_conversation_conv(this);
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
