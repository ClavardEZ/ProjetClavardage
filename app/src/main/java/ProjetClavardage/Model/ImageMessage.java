package ProjetClavardage.Model;


import java.util.Date;

public class ImageMessage extends Message {
    public String file;

    public ImageMessage(Date date, Conversation conversation) {
        super(date,conversation);
    }
}
