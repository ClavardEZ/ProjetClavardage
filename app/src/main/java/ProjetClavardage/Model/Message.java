package ProjetClavardage.Model;

import java.util.Date;

public abstract class Message {
    private Date sentDate;

    private User user;
    private Conversation conversation;


    public Message(Date date, Conversation conv) {
        this.sentDate = date;
        this.conversation = conv;
    }

    public String getContent() {
        return "Il faut implémenter la méthode getContent dans la bonne classe";
    }

}
