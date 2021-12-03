package ProjetClavardage.Controller;

import ProjetClavardage.Model.*;
import ProjetClavardage.View.ButtonTabComponent;
import ProjetClavardage.View.Pan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Date;

public class MainController {
    // TODO link users lists with pan through this class
    // and not link other controllers to user, only through main class
    private ButtonTabComponent btnTabComponent;
    private Pan pan;
    private MessageThreadManager msgThdMngr;
    private PrivateUser privateUser;

    public MainController(int serverPort, int clientPort, int userPort, String username) {
        this.pan = new Pan(this);
        this.msgThdMngr = new MessageThreadManager(this, serverPort, clientPort);
        this.msgThdMngr.start();
        this.privateUser = new PrivateUser(MessageThreadManager.getLocalAdress(), userPort, username);
        // udp
        UserManager userManager = new UserManager(this.privateUser);
        userManager.start_listener();
        userManager.start();
        userManager.sender(true);
    }

    public void openConversation(int index) {
        try {
            System.out.println("IP adress:" + InetAddress.getByName(this.pan.getUsername(index)));
            this.msgThdMngr.openConnection(InetAddress.getByName(this.pan.getUsername(index)));
            //this.msgThdMngr.openConnection(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.pan.addConversationTab(this.msgThdMngr.getConversationsAt(index).getName());
    }

    public void closeConversation(int index) {
        this.msgThdMngr.close_conversation(index);
    }

    public void sendMessage() {
        this.pan.addTextToTabAsSender();
        //Message msg = new TextMessage(LocalDateTime.now(), this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
        Message msg = new TextMessage(LocalDateTime.now(), this.privateUser, this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
        this.msgThdMngr.send(msg, this.pan.getSelectedIndex());
        this.pan.emptyTextField();
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
}
