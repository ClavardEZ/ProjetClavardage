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
        String reqUser = "CREATE TABLE IF NOT EXISTS user (\n" +
                "   ip_address VARCHAR(15),\n" +
                "   username VARCHAR(50),\n" +
                "   PRIMARY KEY(ip_address)\n" +
                ");";
        String reqConversation = "CREATE TABLE IF NOT EXISTS conversation(\n" +
                "   id_conversation CHAR(36),\n" +
                "   conv_name VARCHAR(50), \n" +
                "   PRIMARY KEY(id_conversation)\n" +
                ");\n";
        String reqMessage = "CREATE TABLE IF NOT EXISTS message(\n" +
                "   id_message CHAR(36),\n" +
                "   sent_date DATETIME,\n" +
                "   content VARCHAR(280),\n" +
                "   ip_address VARCHAR(15),\n" +
                "   id_conversation CHAR(36),\n" +
                "   PRIMARY KEY(id_message),\n" +
                "   FOREIGN KEY(ip_address) REFERENCES user(ip_address),\n" +
                "   FOREIGN KEY(id_conversation) REFERENCES conversation(id_conversation)\n" +
                ");\n";
        String reqUserInConv = "CREATE TABLE IF NOT EXISTS user_in_conv(\n" +
                "   ip_address VARCHAR(15),\n" +
                "   id_conversation VARCHAR(50),\n" +
                "   id_message CHAR(36),\n" +
                "   FOREIGN KEY(ip_address) REFERENCES user(ip_address),\n" +
                "   FOREIGN KEY(id_conversation) REFERENCES conversation(id_conversation),\n" +
                "   FOREIGN KEY(id_message) REFERENCES message(id_message), " +
                "   PRIMARY KEY(ip_address, id_conversation, id_message)\n" +
                ");";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute(reqUser);
            stmt.execute(reqConversation);
            stmt.execute(reqMessage);
            stmt.execute(reqUserInConv);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
        String req = "INSERT INTO user (ip_address, username)" +
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
        String req = "INSERT INTO message (id_message, sent_date, content, ip_address, id_conversation)" +
                "VALUES(?, ?, ?, ?, ?);";
        try {
            PreparedStatement pstmt = DatabaseManager.conn.prepareStatement(req);

            pstmt.setString(1, message.getId().toString());
            pstmt.setTimestamp(2, Timestamp.valueOf(message.getDate()));
            pstmt.setString(3, message.getContent());
            pstmt.setString(4, message.getIP().getHostAddress());
            pstmt.setString(5, message.getConvId().toString());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addConversation(Conversation conversation) {
        String req = "INSERT INTO conversation (id_conversation, conv_name)" +
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
            String[] reqs = {"DROP TABLE IF EXISTS user;",
                    "DROP TABLE IF EXISTS conversation;",
                    "DROP TABLE IF EXISTS message;",
                    "DROP TABLE IF EXISTS user_in_conv;"};
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
            String[] reqs = {"DELETE TABLE user;",
                    "DELETE TABLE conversation;",
                    "DELETE TABLE message;",
                    "DELETE TABLE user_in_conv"};
            for (int i = 0; i < reqs.length; i++) {
                stmt.execute(reqs[i]);
            }
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUser(InetAddress ip_address) {
        String req = "SELECT *" +
                "FROM user " +
                "WHERE ip_address = ?;";
        User user = null;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, ip_address.getHostAddress());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(InetAddress.getByName(rs.getString("ip_address")), -1, rs.getString("username"));
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
                "FROM conversation " +
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

    public static Message getMessage(UUID messageId, boolean isText) {
        String req = "SELECT *" +
                "FROM message " +
                "WHERE id_message = ?;";
        Message message = null;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, messageId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("rs next");
                if (isText) {
                    message = new TextMessage(rs.getTimestamp("sent_date").toLocalDateTime(),
                            DatabaseManager.getUser(InetAddress.getByName(rs.getString("ip_address"))),
                            DatabaseManager.getConversation(UUID.fromString(rs.getString("id_conversation"))),
                            UUID.fromString(rs.getString("id_message")),
                            rs.getString("content"));
                }
                rs.close();
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return message;
    }

}
