package ProjetClavardage.Controller;

import ProjetClavardage.View.Pan;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 *Classe gérant la liste des contacts connectés
 */
public class ControllerContactList implements MouseListener {

    private MainController mc;

    public ControllerContactList(MainController mc) {
        this.mc = mc;
    }

    @Override
    /**
     * Lorsque l'on clique sur un contact, on regarde si l'utilisateur a bien configuré son interface réseau avant de démarer la conversation
     */
    public void mouseClicked(MouseEvent e) {
        JList list =(JList) e.getSource();
        int index = -1;
        if (e.getClickCount() == 2) {
            if (!mc.getNi().equals("lo")) {
                index = list.getSelectedIndex();
                System.out.println("index UI = " + index);
                this.mc.openConversation(index);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
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
