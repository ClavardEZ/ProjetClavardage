package ProjetClavardage.Model;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import javax.swing.plaf.nimbus.State;
import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
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
        String reqUser = """
        CREATE TABLE IF NOT EXISTS user(
            ip_address VARCHAR(15),
            username VARCHAR(50),
            PRIMARY KEY(ip_address)
        );
        """;
        String reqConversation = """
        CREATE TABLE IF NOT EXISTS conversation(
            conv_id CHAR(36),
            ip_address VARCHAR(15),
            conv_name VARCHAR(50),
            PRIMARY KEY(conv_id),
            FOREIGN KEY(ip_address) REFERENCES user(ip_address)  
        );
        """;
        String reqMessage = """
        CREATE TABLE IF NOT EXISTS message(
            id_message CHAR(36),
            sent_date DATETIME,
            content VARCHAR(280),
            ip_address VARCHAR(15),
            conv_id CHAR(36),
            PRIMARY KEY(id_message),
            FOREIGN KEY(ip_address) REFERENCES user(ip_address),
            FOREIGN KEY(conv_id) REFERENCES conversation(conv_id)
        );
        """;

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute(reqUser);
            stmt.execute(reqConversation);
            stmt.execute(reqMessage);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(User user) {
        String req = """
            INSERT INTO user (ip_address, username)
            VALUES(?, ?);
        """;
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
        String req = """
            INSERT INTO message(id_message, sent_date, content, ip_address, conv_id)
            VALUES(?, ?, ?, ?, ?);
        """;
        try {
            PreparedStatement pstmt = DatabaseManager.conn.prepareStatement(req);
            pstmt.setString(1, message.getId().toString());
            pstmt.setTimestamp(2, Timestamp.valueOf(message.getDate()));
            pstmt.setString(3, message.getContent());
            pstmt.setString(4, message.getIP().getHostAddress());
            pstmt.setString(5, message.getConvID().toString());
            pstmt.executeUpdate();
            System.out.println("message added to database");
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addConversation(Conversation conversation, InetAddress ip_address) {
        String req = "INSERT INTO conversation (conv_id, ip_address, conv_name)" +
                "VALUES(?, ?, ?);";
        try {
            PreparedStatement pstmt = DatabaseManager.conn.prepareStatement(req);

            pstmt.setString(1, conversation.getID().toString());
            pstmt.setString(2, ip_address.getHostAddress());
            pstmt.setString(3, conversation.getConvName());
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
            stmt.execute("DELETE FROM message;");
            stmt.execute("DELETE FROM conversation;");
            stmt.execute("DELETE FROM user;");
            stmt.execute("DELETE FROM user_in_conv;");
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

    public static Conversation getConversation(UUID convId, InetAddress ip_address, MessageThreadManager msgThdMngr) {
        String req = "SELECT *" +
                "FROM conversation " +
                "WHERE conv_id = ?;";
        Conversation conv = null;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, convId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                conv = new Conversation(rs.getString("conv_name"), msgThdMngr, convId);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conv;
    }

    public static Message getMessage(UUID messageId, boolean isText, MessageThreadManager msgThdMngr, UUID convId) {
        String req = "SELECT *" +
                "FROM message " +
                "WHERE id_message = ?;";
        Message message = null;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, messageId.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                if (isText) {
                    message = new TextMessage(rs.getTimestamp("sent_date").toLocalDateTime(),
                            DatabaseManager.getUser(InetAddress.getByName(rs.getString("ip_address"))),
                            DatabaseManager.getConversation(convId, InetAddress.getByName(rs.getString("ip_address")), msgThdMngr),
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

    public static List<Message> getAllMessagesFromConv(Conversation conv, boolean isText, MessageThreadManager msgThdMngr) {
        ArrayList<Message> messages = new ArrayList<>();

        String req = """
                SELECT *
                FROM message
                WHERE conv_id = ?
                """;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            InetAddress ip_address = null;
            if (conv.getUsersIP().size() > 0) {
                ip_address = conv.getUsersIP().get(0);
            }
            //stmt.setString(1, ip_address.getHostAddress());
            stmt.setString(1, conv.getID().toString());
            ResultSet rs = stmt.executeQuery();
            System.out.println("all messages query");
            while (rs.next()) {
                if (isText) {
                    messages.add(new TextMessage(rs.getTimestamp("sent_date").toLocalDateTime(),
                            DatabaseManager.getUser(InetAddress.getByName(rs.getString("ip_address"))),
                            DatabaseManager.getConversation(conv.getID(), InetAddress.getByName(rs.getString("ip_address")), msgThdMngr),
                            UUID.fromString(rs.getString("id_message")),
                            rs.getString("content")));
                }
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        System.out.println("all messages query done");

        return messages;
    }

    public static void removeUser(InetAddress ip_address) {
        String req = "DELETE FROM user" +
                " WHERE ip_address = ?;";
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, ip_address.getHostAddress());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeConversation(InetAddress ip_address) {
        String req = "DELETE FROM conversation" +
                " WHERE id_conversation = ?";
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, ip_address.getHostAddress());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeMessage(UUID messageId, boolean isText) {
        String req = "DELETE FROM message" +
                " WHERE id_message = ?";
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            stmt.setString(1, messageId.toString());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Message> searchMessageBytext(Conversation conv, String text, MessageThreadManager msgThdMngr) {
        ArrayList<Message> messages = new ArrayList<>();

        String req = """
                SELECT *
                FROM message
                WHERE ip_address = ?
                AND content like ?;
                """;
        try {
            PreparedStatement stmt = DatabaseManager.conn.prepareStatement(req);
            InetAddress ipAddress = null;
            if (conv.getUsersIP().size() > 0) {
                ipAddress = conv.getUsersIP().get(0);
            }
            stmt.setString(1, ipAddress.getHostAddress());
            stmt.setString(2, "%" + text + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new TextMessage(rs.getTimestamp("sent_date").toLocalDateTime(),
                        DatabaseManager.getUser(InetAddress.getByName(rs.getString("ip_address"))),
                        DatabaseManager.getConversation(conv.getID(), InetAddress.getByName(rs.getString("ip_address")), msgThdMngr),
                        UUID.fromString(rs.getString("id_message")),
                        rs.getString("content")));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return messages;
    }

}
