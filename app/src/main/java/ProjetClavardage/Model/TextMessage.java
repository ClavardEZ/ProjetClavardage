package ProjetClavardage.Model;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class TextMessage extends Message {
    public String content;

    @Deprecated
    public TextMessage(LocalDateTime date, Conversation conversation, String content) {
        super(date,conversation);
        this.content = content;
    }
    /**
     * Nouveau message
     * @param sentDate date d'envoi
     * @param user expéditeur
     * @param conversation conversation associée au message
     * @param content texte du message
     */
    public TextMessage(LocalDateTime sentDate, User user, Conversation conversation, String content) {
        super(sentDate, user, conversation);
        this.content = content;
    }

    /**
     * Message chargé depus la base de donnée
     * @param sentDate date d'envoi
     * @param user
     * @param conversation
     * @param id
     * @param content
     */
    public TextMessage(LocalDateTime sentDate, User user, Conversation conversation, UUID id, String content) {
        super(sentDate, user, conversation, id);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TextMessage that = (TextMessage) o;
        return Objects.equals(content, that.content);
    }
}
