package ProjetClavardage.Model;


import java.time.LocalDateTime;
import java.util.Date;

/**
 * Non implémentée
 */
public class ImageMessage extends Message {
    public String file;

    public ImageMessage(LocalDateTime date, Conversation conversation) {
        super(date,conversation);
    }

    @Override
    @Deprecated
    public String getContent() {
        return null;
    }
}
