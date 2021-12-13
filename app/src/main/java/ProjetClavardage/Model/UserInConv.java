package ProjetClavardage.Model;

import java.io.*;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.*;

public class UserInConv extends Thread{
    Socket sock;
    MessageThreadManager msgThMng;
    InputStream iStream;
    OutputStream oStream;
    Conversation conv;
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

    public void run() {
        try {
        Message msg = null;
        ObjectInputStream oiStream = null;
        do {
            oiStream = new ObjectInputStream(this.iStream);
            msg = (Message) oiStream.readObject();
            if (msg != null) {
                System.out.println("received msg=" + msg.toString());
                this.msgThMng.received(msg, this.conv);
            }
        } while (msg != null);
        System.out.println("closed");
        this.msgThMng.close_conversation_conv(this.conv);
    } catch (SocketException e) {
        System.out.println("Client disconnected");
    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();
    }

    }
}
