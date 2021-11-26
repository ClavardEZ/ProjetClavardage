package ProjetClavardage.Controller;

import ProjetClavardage.Model.PopupNewConv;
import ProjetClavardage.View.Pan;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ControllerAddConversation implements ActionListener {

    private Pan pan;
    private DefaultListModel<String> contacts;

    public ControllerAddConversation(Pan pan, DefaultListModel<String> contacts) {
        this.pan = pan;
        this.contacts = contacts;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new PopupNewConv(pan, this.pan.getParent().getSize());
    }
}
