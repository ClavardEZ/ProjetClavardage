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

    private User user;
    private Conversation conv;
    private Message message;

    @Before
    public void setUp() throws Exception {
        DatabaseManager.connect();
        DatabaseManager.createTables();
        //this.conv = new Conversation("Conv #1", null, null);
        this.user = new User(InetAddress.getLocalHost(), 0, "user");
        this.message = new TextMessage(LocalDateTime.now(), this.user, this.conv, "message");
    }

    @After
    public void tearDown() throws Exception {
        //DatabaseManager.flushTableData();
        DatabaseManager.closeConnection();
    }

    @Test
    public void testAddMessage() {
        DatabaseManager.addUser(this.user);
        DatabaseManager.addConversation(this.conv);
        DatabaseManager.addMessage(this.message);
    }
}