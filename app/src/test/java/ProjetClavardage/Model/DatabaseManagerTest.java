package ProjetClavardage.Model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseManagerTest extends TestCase {

    private User user;
    private Conversation conv;
    private Message message;
    private InetAddress inetAddress;

    @Before
    public void setUp() throws Exception {
        DatabaseManager.connect();
        DatabaseManager.createTables();

        // creating
        this.inetAddress = InetAddress.getLocalHost();
        this.user = new User(this.inetAddress, 0, "user");
        ArrayList<InetAddress> addresses = new ArrayList<>();
        addresses.add(this.user.getIP());
        this.conv = new Conversation("Conv #1", null, null, addresses);
        this.message = new TextMessage(LocalDateTime.now(), this.user, this.conv, "message");

        // inserting
        DatabaseManager.addUser(this.user);
        DatabaseManager.addConversation(this.conv);
        DatabaseManager.addMessage(this.message);
    }

    @After
    public void tearDown() {
        DatabaseManager.deleteTables();
        DatabaseManager.closeConnection();
    }

    @Test
    public void testGetUser() throws UnknownHostException {
        User reqResult = DatabaseManager.getUser(this.inetAddress);
        assertNotNull(reqResult);
        assertEquals(this.user.getIP(), reqResult.getIP());
        assertEquals(this.user.getUsername(), reqResult.getUsername());
    }

    @Test
    public void testGetConversation() {
        Conversation reqResult = DatabaseManager.getConversation(this.conv.getID());
        assertNotNull(reqResult);
        assertEquals(this.conv.getID(), reqResult.getID());
        assertEquals(this.conv.getConvName(), reqResult.getConvName());
    }
}