package ProjetClavardage.Controller;

import ProjetClavardage.View.Pan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerSendMessage implements ActionListener {

    private Pan parent;

    public ControllerSendMessage(Pan parent) {
        this.parent = parent;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("sent message");
        this.parent.sendMessage();
    }
}
