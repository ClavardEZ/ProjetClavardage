package ProjetClavardage.Controller;

import ProjetClavardage.View.PopupNewConv;
import ProjetClavardage.View.Pan;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerAddConversation implements ActionListener {

    private Pan pan;
    private DefaultListModel<String> contacts;
    private MainController mc;

    public ControllerAddConversation(MainController mc, Pan pan, DefaultListModel<String> contacts) {
        this.pan = pan;
        this.contacts = contacts;
        this.mc = mc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new PopupNewConv(this.mc, pan, this.pan.getParent().getSize());
    }
}
