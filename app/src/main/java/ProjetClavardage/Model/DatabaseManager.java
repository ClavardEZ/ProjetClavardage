package ProjetClavardage.Model;

import javax.xml.crypto.Data;
import java.sql.*;

public class DatabaseManager {

    // also connects to the database
    public static Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:database.sqlite";
            conn = DriverManager.getConnection(url);
            System.out.println("Connexion a la base de donnees etablie");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void createTables() {
        String reqAppUser = "CREATE TABLE IF NOT EXISTS AppUser (\n" +
                "   login VARCHAR(50),\n" +
                "   username VARCHAR(50),\n" +
                "   PRIMARY KEY(login)\n" +
                ");";
        String reqConversation = "CREATE TABLE IF NOT EXISTS Conversation(\n" +
                "   id_conversation VARCHAR(50),\n" +
                "   PRIMARY KEY(id_conversation)\n" +
                ");\n";
        String reqMessage = "CREATE TABLE IF NOT EXISTS Message(\n" +
                "   sent_date DATETIME,\n" +
                "   content VARCHAR(50),\n" +
                "   login VARCHAR(50) NOT NULL,\n" +
                "   PRIMARY KEY(sent_date),\n" +
                "   FOREIGN KEY(login) REFERENCES AppUser(login)\n" +
                ");\n";
        String reqUserInConv = "CREATE TABLE IF NOT EXISTS User_in_conv(\n" +
                "   login VARCHAR(50),\n" +
                "   id_conversation VARCHAR(50),\n" +
                "   sent_date DATETIME,\n" +
                "   PRIMARY KEY(login, id_conversation, sent_date),\n" +
                "   FOREIGN KEY(login) REFERENCES AppUser(login),\n" +
                "   FOREIGN KEY(id_conversation) REFERENCES Conversation(id_conversation),\n" +
                "   FOREIGN KEY(sent_date) REFERENCES Message(sent_date)\n" +
                ");";

        Statement stmt = null;
        try {
            Connection conn = connect();
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

    public static void addUser(String login, String username) {
        String req = "INSERT INTO AppUser (login, username)" +
                "VALUES(?, ?);";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(req);
            pstmt.setString(1, "user1");
            pstmt.setString(2, "MonsieurSinge");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // not working
    public static void createNewDatabase() {
        //String dataFolder = System.getProperty("user.home") + "\\Local Settings\\ApplicationData\\ClavardEZ\\database.db";
        // for windows only
        String dataFolder = System.getenv("APPDATA");
        String url = "jdbc:sqlite:" + dataFolder + "\\ClavardEZ\\database.db";
        try (Connection conn = DriverManager.getConnection(url)){
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Driver name : " + meta.getDriverName());
                System.out.println("New database created");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
