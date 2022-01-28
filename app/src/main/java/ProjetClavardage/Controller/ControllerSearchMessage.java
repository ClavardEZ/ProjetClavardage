package ProjetClavardage.Controller;

import ProjetClavardage.View.Pan;
import ProjetClavardage.View.PopupSearchMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Classe gérant la popup de recherche de message
 */
public class ControllerSearchMessage implements ActionListener {

    private Pan parent;
    private MainController mc;

    public ControllerSearchMessage(Pan parent, MainController mc) {
        this.parent = parent;
        this.mc = mc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.parent.getSelectedIndex() >= 0) {
            new PopupSearchMessage(this.parent, this.mc, this.parent.getSize());
        }
    }
}
