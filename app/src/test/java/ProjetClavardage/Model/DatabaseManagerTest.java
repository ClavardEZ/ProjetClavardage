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
        DatabaseManager.createNewDatabase();
    }

    @After
    public void tearDown() throws Exception {
        DatabaseManager.closeConnection();
    }

    @Test
    public void testCreateTables() {
        DatabaseManager.createTables();
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
        DatabaseManager.addMessage(new TextMessage(LocalDateTime.now(), new Conversation(), "message"));
    }

}