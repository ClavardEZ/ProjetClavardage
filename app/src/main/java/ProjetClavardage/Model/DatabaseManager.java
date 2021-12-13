package ProjetClavardage.Model;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import javax.swing.plaf.nimbus.State;
import javax.xml.crypto.Data;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.ZoneId;
import java.util.UUID;

public final class DatabaseManager {

    private static Connection conn;

    private DatabaseManager() {}

    // not working
    public static boolean connect() {
        // merci à roro le sang aka Xx_MechaFeeder13_xX
        AppDirs appDirs = AppDirsFactory.getInstance();
        String dataFolder = appDirs.getUserDataDir("ClavardEZ", null, "Clavardeurs");
        (new File(dataFolder)).mkdirs();

        String url = "jdbc:sqlite:" + dataFolder + File.separator + "database.db";
        try {
            DatabaseManager.conn = DriverManager.getConnection(url);
            if (conn != null) {
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
                "   id_message CHAR(36)" +
                "   sent_date DATETIME,\n" +
                "   content VARCHAR(280),\n" +
                "   ipaddress VARCHAR(15),\n" +
                "   id_conversation CHAR(36),\n" +
                "   PRIMARY KEY(id_message),\n" +
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
                "   FOREIGN KEY(id_message) REFERENCES Message(id_message)\n" +
                ");";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute(reqAppUser);
            stmt.execute(reqConversation);
            stmt.execute(reqMessage);
            stmt.execute(reqUserInConv);
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
            pstmt.setString(1, user.getIP().getHostAddress());
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
        String req = "INSERT INTO Conversation (id_conversation, conv_name)" +
                "VALUES(?, ?);";
        try {
            PreparedStatement pstmt = DatabaseManager.conn.prepareStatement(req);

            pstmt.setString(1, conversation.getID().toString());
            pstmt.setString(2, conversation.getConvName());
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
            String[] reqs = {"DROP TABLE IF EXISTS AppUser;",
                    "DROP TABLE IF EXISTS Conversation;",
                    "DROP TABLE IF EXISTS Message;",
                    "DROP TABLE IF EXISTS User_in_conv;"};
            for (int i = 0; i < reqs.length; i++) {
                stmt.execute(reqs[i]);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void flushTableData() {
        try {
            Statement stmt = DatabaseManager.conn.createStatement();
            String[] reqs = {"DELETE TABLE AppUser;",
                    "DELETE TABLE Conversation;",
                    "DELETE TABLE Message;",
                    "DELETE TABLE User_in_conv"};
            for (int i = 0; i < reqs.length; i++) {
                stmt.execute(reqs[i]);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUser(InetAddress ipaddress) {
        String req = "SELECT *" +
                "FROM AppUser " +
                "WHERE ipaddress = ?;";
        User user = null;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, ipaddress.getHostAddress());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(InetAddress.getByName(rs.getString("ipaddress")), -1, rs.getString("username"));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static Conversation getConversation(UUID convId) {
        String req = "SELECT *" +
                "FROM Conversation " +
                "WHERE id_conversation = ?";
        Conversation conv = null;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, convId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                conv = new Conversation(rs.getString("conv_name"), null, null, UUID.fromString(rs.getString("id_conversation")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conv;
    }

    // TODO
    public static Message getMessage() {
        return null;
    }

}
