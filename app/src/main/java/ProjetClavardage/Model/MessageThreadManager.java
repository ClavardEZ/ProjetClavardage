package ProjetClavardage.Model;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe gérant l'envoi et la reception de messages, et gère les conversations ainsi que la base de données
 */
public class MessageThreadManager {

    private ArrayList<UserInConv> userInConvs;

    public MessageThreadManager() {
        this.userInConvs = new ArrayList<>();
    }

    public void OpenConnection() {
    }

    public void Send(Message msg) {
    }

}
