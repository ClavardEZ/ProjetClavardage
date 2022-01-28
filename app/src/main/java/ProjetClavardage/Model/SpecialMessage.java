package ProjetClavardage.Model;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
/**
 * Classe servant à partager les informations des conversations
 */
public class SpecialMessage extends Message{
    private ArrayList<InetAddress> usersIP;
    public SpecialMessage(Conversation conversation) {
        super(LocalDateTime.now(),conversation);
        this.usersIP = conversation.getUsersIP();
    }
    public ArrayList<InetAddress> getUsersIP(){return this.usersIP;}

    @Override
    @Deprecated
    public String getContent() {
        return null;
    }
}
