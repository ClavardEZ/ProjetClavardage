package ProjetClavardage.Model;

import javax.xml.crypto.Data;
import java.net.InetAddress;
import java.sql.*;
import java.time.ZoneId;

public class DatabaseManager {

    private static Connection conn;

    // also connects to the database
    /*public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:database.sqlite";
            conn = DriverManager.getConnection(url);
            System.out.println("Connexion a la base de donnees etablie");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }*/

    // not working
    public static boolean createNewDatabase() {
        //String dataFolder = System.getProperty("user.home") + "\\Local Settings\\ApplicationData\\ClavardEZ\\database.db";
        // for windows only
        String dataFolder = System.getenv("APPDATA");
        String url = "jdbc:sqlite:" + dataFolder + "\\ClavardEZ\\database.db";
        System.out.println("local database url : " + url);

        try {
            DatabaseManager.conn = DriverManager.getConnection(url);
            if (conn != null) {
                System.out.println("New Database created/connection established to the database");
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void createTables() {
        String reqAppUser = "CREATE TABLE IF NOT EXISTS AppUser (\n" +
                "   ipaddress VARCHAR(15),\n" +
                "   username VARCHAR(50),\n" +
                "   PRIMARY KEY(ipaddress)\n" +
                ");";
        String reqConversation = "CREATE TABLE IF NOT EXISTS Conversation(\n" +
                "   id_conversation VARCHAR(50),\n" +
                "   conv_name VARCHAR(50), \n" +
                "   PRIMARY KEY(id_conversation)\n" +
                ");\n";
        String reqMessage = "CREATE TABLE IF NOT EXISTS Message(\n" +
                "   sent_date DATETIME,\n" +
                "   content VARCHAR(280),\n" +
                "   PRIMARY KEY(sent_date),\n" +
                "   FOREIGN KEY(ipaddress) REFERENCES AppUser(ipaddress)\n" +
                "   FOREIGN KEY(id_conversation) REFERENCES Conversation(id_conversation)\n" +
                ");\n";
        String reqUserInConv = "CREATE TABLE IF NOT EXISTS User_in_conv(\n" +
                "   ipaddress VARCHAR(15),\n" +
                "   id_conversation VARCHAR(50),\n" +
                "   sent_date DATETIME,\n" +
                "   PRIMARY KEY(ipaddress, id_conversation, sent_date),\n" +
                "   FOREIGN KEY(ipaddress) REFERENCES AppUser(ipaddress),\n" +
                "   FOREIGN KEY(id_conversation) REFERENCES Conversation(id_conversation),\n" +
                "   FOREIGN KEY(sent_date) REFERENCES Message(sent_date)\n" +
                ");";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute(reqAppUser);
            System.out.println("AppUser table created succesfully");
            stmt.execute(reqConversation);
            System.out.println("Conversation table created succesfully");
            stmt.execute(reqMessage);
            System.out.println("Message table created succesfully");
            stmt.execute(reqUserInConv);
            System.out.println("UserInConv table created succesfully");
            System.out.println("Tables created succesfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(InetAddress ipaddress, String username) {
        String req = "INSERT INTO AppUser (ipaddress, username)" +
                "VALUES(?, ?, ?);";
        try {
            PreparedStatement pstmt = conn.prepareStatement(req);
            pstmt.setString(1, ipaddress.toString());
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addMessage(Message message) {
        String req = "INSERT INTO Message (sent_date, content, ipaddress, id_conversation)" +
                "VALUES(?, ?, ?, ?);";
        try {
            PreparedStatement pstmt = conn.prepareStatement(req);

            pstmt.setTimestamp(1, Timestamp.valueOf(message.getDate()));
            pstmt.setString(2, message.getContent());
            pstmt.setString(3, message.getIP().toString());
            pstmt.setString(4, message.getConvId().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean closeConnection() {
        try {
            DatabaseManager.conn.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}
