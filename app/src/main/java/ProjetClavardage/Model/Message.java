package ProjetClavardage.Model;
import java.util.UUID;
import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Date;

public abstract class Message implements Serializable {
    private LocalDateTime sentDate;

    private User user;
    private transient Conversation conversation;

    public Message(LocalDateTime date, Conversation conv) {
        this.sentDate = date;
        this.conversation = conv;
        this.user = null;
    }

    public Message(LocalDateTime sentDate, User user, Conversation conversation) {
        this.sentDate = sentDate;
        this.user = user;
        this.conversation = conversation;
    }

    public String getContent() {
        return "Il faut implémenter la méthode getContent dans la bonne classe";
    }
    public LocalDateTime getDate(){return this.sentDate;}
    public InetAddress getIP(){return this.user.getIP();}
    public UUID getConvId(){return this.conversation.getID();}

    @Override
    public String toString() {
        return "Message{" +
                "sentDate=" + sentDate +
                ", user=" + user +
                ", conversation=" + conversation +
                '}';
    }

    public User getUser() {
        return user;
    }
}
