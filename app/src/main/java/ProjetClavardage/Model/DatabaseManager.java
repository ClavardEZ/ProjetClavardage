package ProjetClavardage.Model;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import javax.xml.crypto.Data;
import java.io.File;
import java.net.InetAddress;
import java.sql.*;
import java.time.ZoneId;

public final class DatabaseManager {

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

    private DatabaseManager() {}

    // not working
    public static boolean connect() {
        // merci à roro le sang aka Xx_MechaFeeder13_xX
        AppDirs appDirs = AppDirsFactory.getInstance();
        String dataFolder = appDirs.getUserDataDir("ClavardEZ", null, "Clavardeurs");
        (new File(dataFolder)).mkdirs();

        String url = "jdbc:sqlite:" + dataFolder + File.separator + "database.db";
        System.out.println("database location : " + url);
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
                "   id_conversation CHAR(36),\n" +
                "   conv_name VARCHAR(50), \n" +
                "   PRIMARY KEY(id_conversation)\n" +
                ");\n";
        String reqMessage = "CREATE TABLE IF NOT EXISTS Message(\n" +
                "   sent_date DATETIME,\n" +
                "   content VARCHAR(280),\n" +
                "   ipaddress VARCHAR(15),\n" +
                "   id_conversation CHAR(36),\n" +
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
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
        String req = "INSERT INTO AppUser (ipaddress, username)" +
                "VALUES(?, ?);";
        try {
            PreparedStatement pstmt = conn.prepareStatement(req);
            pstmt.setString(1, user.getIP().toString());
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addMessage(Message message) {
        String req = "INSERT INTO Message (sent_date, content, ipaddress, id_conversation)" +
                "VALUES(?, ?, ?, ?);";
        try {
            PreparedStatement pstmt = DatabaseManager.conn.prepareStatement(req);

            pstmt.setTimestamp(1, Timestamp.valueOf(message.getDate()));
            pstmt.setString(2, message.getContent());
            pstmt.setString(3, message.getIP().toString());
            pstmt.setString(4, message.getConvId().toString());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addConversation(Conversation conversation) {
        String req = "INSERT INTO Message (sent_date, id)" +
                "VALUES(?, ?);";
        try {
            PreparedStatement pstmt = DatabaseManager.conn.prepareStatement(req);

            pstmt.setString(1, conversation.getConvName());
            pstmt.setString(2, conversation.getID().toString());
            pstmt.executeUpdate();
            pstmt.close();
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

    // TODO execute multiple queries
    public static void deleteTables() {
        try {
            Statement stmt = DatabaseManager.conn.createStatement();
            String req = "DROP TABLE IF EXISTS AppUser;" +
                    "DROP TABLE IF EXISTS Conversation;" +
                    "DROP TABLE IF EXISTS Message;" +
                    "DROP TABLE IF EXISTS User_in_conv;";
            stmt.execute(req);
            stmt.close();
            System.out.println("Tables deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void flushTableData() {
        try {
            Statement stmt = DatabaseManager.conn.createStatement();
            String req = "TRUNCATE TABLE AppUser;" +
                    "TRUNCATE TABLE Conversation;" +
                    "TRUNCATE TABLE Message;" +
                    "TRUNCATE TABLE User_in_conv;";
            stmt.execute(req);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
