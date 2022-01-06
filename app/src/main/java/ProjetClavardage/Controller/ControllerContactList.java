package ProjetClavardage.Controller;

import ProjetClavardage.View.Pan;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ControllerContactList implements MouseListener {

    private MainController mc;

    public ControllerContactList(MainController mc) {
        this.mc = mc;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JList list =(JList) e.getSource();
        int index = -1;
        if (e.getClickCount() == 2) {
            index = list.locationToIndex(e.getPoint());
            this.mc.openConversation(index);
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
       /* //TODO faire un truc plus propre
        JList list =(JList) e.getSource();
        int index = -1;
        index = list.locationToIndex(e.getPoint());
        this.mc.addUserInConv(index);
*/
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
