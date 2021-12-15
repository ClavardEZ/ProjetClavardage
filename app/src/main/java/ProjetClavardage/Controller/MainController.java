package ProjetClavardage.Controller;

import ProjetClavardage.Model.*;
import ProjetClavardage.View.ButtonTabComponent;
import ProjetClavardage.View.Pan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainController {
    // TODO link users lists with pan through this class
    // and not link other controllers to user, only through main class
    private ButtonTabComponent btnTabComponent;
    private Pan pan;
    private MessageThreadManager msgThdMngr;
    private PrivateUser privateUser;
    private UserManager userManager;
    private HashMap<String, User> usersByUsername;

    public MainController(int serverPort, int clientPort, int listeningPort, int sendingPort, String username) {
        this.pan = new Pan(this);
        this.msgThdMngr = new MessageThreadManager(this, serverPort, clientPort);
        this.msgThdMngr.start();
        this.privateUser = new PrivateUser(MessageThreadManager.getLocalAddress(), username);

        // udp
        this.usersByUsername = new HashMap<>();

        this.userManager = new UserManager(this, this.privateUser, listeningPort, sendingPort);
        this.userManager.start_listener();
        this.userManager.start();
        this.userManager.sender(true);
    }

    public String getUsernameByIP(InetAddress ip) {
        return this.userManager.getUserByIP(ip).getUsername();
    }

    public void addUser(User user) {
        if (!this.usersByUsername.containsKey(user.getUsername())) {
            this.usersByUsername.put(user.getUsername(), user);
            // ajout à a vue
            // can be called directly
            this.addContact(user.getUsername());
        }
    }

    public void removeUser(User user) {
        System.out.println("user removed from mc");
        this.usersByUsername.remove(user.getUsername());
        this.pan.removeContact(user.getUsername());
    }

    public void openConversation(int index) {

        /*System.out.println("IP adress:" + InetAddress.getByName(this.pan.getUsername(index)));
        this.msgThdMngr.openConnection(InetAddress.getByName(this.pan.getUsername(index)));*/

        this.msgThdMngr.openConnection(this.usersByUsername.get(this.pan.getUsername(index)).getIP(), this.pan.getUsername(index),new Conversation("lala", msgThdMngr));
        System.out.println("IP address : " + this.usersByUsername.get(this.pan.getUsername(index)).getIP());

        //this.msgThdMngr.openConnection(InetAddress.getLocalHost());

        this.pan.addConversationTab(this.msgThdMngr.getConversationsAt(index).getName());
    }

    public void closeConversation(int index) {
        this.msgThdMngr.close_conversation(index);
    }

    public void sendMessage() {
        if (!this.pan.getTextfieldText().isBlank()) {
            this.pan.addTextToTabAsSender();
            //Message msg = new TextMessage(LocalDateTime.now(), this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
            Message msg = new TextMessage(LocalDateTime.now(), this.privateUser, this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
            this.msgThdMngr.send(msg, this.pan.getSelectedIndex());
            this.pan.emptyTextField();
        }
    }

    public void addConversationTab(String title) {
        this.pan.addConversationTab(title);
    }

    public void addContact(String username) {
        this.pan.addContact(username);
    }

    public void removeConversationTab(int index) {
        this.pan.removeConversationTab(index);
    }

    public void addTextToTab(int index, String text) {
        this.pan.addTextToTab(index, text);
    }

    public Pan getPan() {
        return pan;
    }

    public MessageThreadManager getMsgThdMngr() {
        return msgThdMngr;
    }

    public String getPrivateUsername() {
        return this.privateUser.getUsername();
    }

    public boolean isPlaceholderText() {
        return this.pan.isPlaceholderText();
    }
}
