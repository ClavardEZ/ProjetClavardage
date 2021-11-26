package ProjetClavardage.Model;
import java.util.Date;

public class TextMessage extends Message {
    public String content;

    public TextMessage(Date date, Conversation conversation, String content) {
        super(date,conversation);
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}
