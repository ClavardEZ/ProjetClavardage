package ProjetClavardage.Controller;

import ProjetClavardage.Model.Message;
import ProjetClavardage.Model.MessageThreadManager;
import ProjetClavardage.Model.TextMessage;
import ProjetClavardage.View.ButtonTabComponent;
import ProjetClavardage.View.Pan;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

public class MainController {
    // TODO link users lists with pan through this class
    // and not link other controllers to user, only through main class
    private ButtonTabComponent btnTabComponent;
    private Pan pan;
    private MessageThreadManager msgThdMngr;

    public MainController(int serverPort, int clientPort) {
        this.pan = new Pan(this);
        this.msgThdMngr = new MessageThreadManager(this, serverPort, clientPort);
        this.msgThdMngr.start();
    }

    public void openConversation(int index) {
        try {
            System.out.println("IP adress:" + InetAddress.getByName(this.pan.getUsername(index)));
            this.msgThdMngr.openConnection(InetAddress.getByName(this.pan.getUsername(index)));
            //this.msgThdMngr.openConnection(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.pan.addConversationTab(this.pan.getUsername(index));
    }

    public void closeConversation(int index) {
        this.msgThdMngr.close_conversation(index);
    }

    public void sendMessage() {
        this.pan.addTextToTabAsSender();
        Message msg = new TextMessage(new Date(), this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
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
}
