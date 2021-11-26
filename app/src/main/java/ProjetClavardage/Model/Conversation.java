package ProjetClavardage.Model;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Conversation extends Thread{
    Socket sock;
    public static final int MSG_LENGTH = 280;

    public Conversation (String str, Socket s) {
        super(str);
        sock = s;
    }
    public void close_connection () {
        try {
            this.sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){
        try {
            InputStream iStream = sock.getInputStream();
            OutputStream oStream = sock.getOutputStream();
            String msg = "test\n";

            oStream.write(msg.getBytes());
            byte[] data= new byte[MSG_LENGTH];
            iStream.read(data);
            String received_msg = new String(data);

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
