package ProjetClavardage.Controller;

import ProjetClavardage.View.PopupChangeUsername;
import ProjetClavardage.View.Pan;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerChangeUsername implements ActionListener {

    private Pan pan;
    private DefaultListModel<String> contacts;
    private MainController mc;

    public ControllerChangeUsername(MainController mc, Pan pan, DefaultListModel<String> contacts) {
        this.pan = pan;
        this.contacts = contacts;
        this.mc = mc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new PopupChangeUsername(this.mc, pan, this.pan.getParent().getSize());
    }
}
