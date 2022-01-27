package ProjetClavardage.Controller;

import ProjetClavardage.View.Pan;
import ProjetClavardage.View.PopupSelectNetInterface;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControllerChangeNetInterface implements ActionListener {

    private Pan parent;
    private MainController mc;

    public ControllerChangeNetInterface(Pan parent, MainController mc) {
        this.parent = parent;
        this.mc = mc;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new PopupSelectNetInterface(this.mc, this.parent, this.parent.getParent().getSize());
    }
}
