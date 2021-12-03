package ProjetClavardage.Model;
import java.time.LocalDateTime;
import java.util.Date;

public class TextMessage extends Message {
    public String content;

    public TextMessage(LocalDateTime date, Conversation conversation, String content) {
        super(date,conversation);
        this.content = content;
    }

    public TextMessage(LocalDateTime sentDate, User user, Conversation conversation, String content) {
        super(sentDate, user, conversation);
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "content='" + content + '\'' +
                super.toString() +
                '}';
    }
}
