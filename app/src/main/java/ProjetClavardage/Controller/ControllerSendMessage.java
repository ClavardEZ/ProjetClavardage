package ProjetClavardage.Controller;

import ProjetClavardage.View.Pan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Classe gérant l'appui sur le bonton "envoyer"
 */
public class ControllerSendMessage implements ActionListener {

    private Pan parent;
    private MainController mc;

    public ControllerSendMessage(Pan parent, MainController mc) {
        this.parent = parent;
        this.mc = mc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!this.mc.isPlaceholderText()) {
            this.mc.sendMessage();
        }
    }
}
