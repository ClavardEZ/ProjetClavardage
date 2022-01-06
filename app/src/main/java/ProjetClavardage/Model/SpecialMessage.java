package ProjetClavardage.Model;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class SpecialMessage extends Message{
    private ArrayList<InetAddress> usersIP;
    public SpecialMessage(Conversation conversation) {
        super(LocalDateTime.now(),conversation);
        this.usersIP = conversation.getUsersIP();
    }
    public ArrayList<InetAddress> getUsersIP(){return this.usersIP;}
}
