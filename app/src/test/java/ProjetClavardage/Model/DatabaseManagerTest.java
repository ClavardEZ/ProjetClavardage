package ProjetClavardage.Model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class DatabaseManagerTest extends TestCase {

    private User user;
    private Conversation conv;
    private Message message;
    private InetAddress inetAddress;
    //private UserInConv userInConv;

    @Override
    @Before
    public void setUp() throws Exception {
        DatabaseManager.connect();
        DatabaseManager.createTables();

        // creating
        this.inetAddress = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
        this.user = new User(this.inetAddress, 0, "user");
        ArrayList<InetAddress> addresses = new ArrayList<>();
        addresses.add(this.user.getIP());
        this.conv = new Conversation("Conv #1", null);
        this.message = new TextMessage(LocalDateTime.now(), this.user, this.conv, "message");
        //this.userInConv = new UserInConv("UserInConv", new Socket(this.inetAddress, 9000), null, this.conv);

        // inserting
        DatabaseManager.addUser(this.user);
        DatabaseManager.addConversation(this.conv);
        DatabaseManager.addMessage(this.message);
        DatabaseManager.addUserInConv(this.inetAddress, this.conv.getID());
    }

    @After
    public void tearDown() {
        DatabaseManager.flushTableData();
        DatabaseManager.closeConnection();
    }

    @Test
    public void testGetUser() throws UnknownHostException {
        User reqResult = DatabaseManager.getUser(this.inetAddress);
        assertNotNull(reqResult);
        assertEquals(this.user, reqResult);
    }

    @Test
    public void testGetConversation() {
        Conversation reqResult = DatabaseManager.getConversation(this.conv.getID(), null);
        assertNotNull(reqResult);
        assertEquals(this.conv.getID(), reqResult.getID());
        assertEquals(this.conv.getConvName(), reqResult.getConvName());
    }

    @Test
    public void testGetMessage() {
        Message reqResult = DatabaseManager.getMessage(this.message.getId(), true, null);
        assertNotNull(reqResult);
        assertEquals(this.message, reqResult);
    }

    @Test
    public void testRemoveUser() {
        DatabaseManager.removeUser(this.user.getIP());
        assertNull(DatabaseManager.getUser(this.user.getIP()));
    }

    @Test
    public void testRemoveConversation() {
        DatabaseManager.removeConversation(this.conv.getID());
        assertNull(DatabaseManager.getConversation(this.conv.getID(), null));
    }

    @Test
    public void testRemoveMessage() {
        DatabaseManager.removeMessage(this.message.getId(), true);
        assertNull(DatabaseManager.getMessage(this.message.getId(), true, null));
    }
}