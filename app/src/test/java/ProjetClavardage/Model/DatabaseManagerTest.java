package ProjetClavardage.Model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class DatabaseManagerTest extends TestCase {

    @Before
    public void setUp() throws Exception {
        DatabaseManager.connect();
        DatabaseManager.createTables();
    }

    @After
    public void tearDown() throws Exception {
        DatabaseManager.deleteTables();
        DatabaseManager.closeConnection();
    }

    @Test
    public void testAddUser() {
        try {
            DatabaseManager.addUser(InetAddress.getLocalHost(), "user");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // add select query to assert
    }

    @Test
    public void testAddMessage() {
        try {
            DatabaseManager.addMessage(new TextMessage(LocalDateTime.now(), new User(InetAddress.getLocalHost(), 9000, "user1"), new Conversation("user 1"), "message"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

}