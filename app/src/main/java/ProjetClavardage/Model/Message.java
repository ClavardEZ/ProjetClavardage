package ProjetClavardage.Model;
import java.util.UUID;
import java.io.Serializable;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Date;

public abstract class Message implements Serializable {
    private LocalDateTime sentDate;
    private UUID convID;

    private User user;
    private transient Conversation conversation;
    private UUID id;

    public Message(LocalDateTime date, Conversation conv) {
        this.sentDate = date;
        this.conversation = conv;
        this.user = null;
        this.convID = this.conversation.getID();
    }

    public Message(LocalDateTime sentDate, User user, Conversation conversation) {
        this.sentDate = sentDate;
        this.user = user;
        this.conversation = conversation;
        this.convID = conversation.getID();
    }

    public Message(LocalDateTime sentDate, User user, Conversation conversation, UUID id) {
        this.sentDate = sentDate;
        this.user = user;
        this.conversation = conversation;
        this.id = id;
        this.convID = conversation.getID();
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return "Il faut implémenter la méthode getContent dans la bonne classe";
    }
    public LocalDateTime getDate(){return this.sentDate;}
    public InetAddress getIP(){return this.user.getIP();}
    public UUID getConvID() {return this.convID;}
    public Conversation getConv(){return this.conversation;}

    @Override
    public String toString() {
        return "Message{" +
                "sentDate=" + sentDate +
                ", user=" + user +
                ", conversationID=" + this.convID.toString() +
                '}';
    }

    public User getUser() {
        return user;
    }
}
