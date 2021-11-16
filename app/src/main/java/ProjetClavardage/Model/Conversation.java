package ProjetClavardage.Model;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Conversation extends Thread{
    ServerSocket servsock;
    Socket sock;

    public Conversation (String str, ServerSocket ss, Socket s) {
        super(str);
        servsock = ss;
        sock = s;
    }

    public void run(){
        try {
            InputStream iStream = sock.getInputStream();
            OutputStream oStream = sock.getOutputStream();
            String msg = "test";
            oStream.write(msg.getBytes());

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
