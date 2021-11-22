package ProjetClavardage.View;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CloseButtonListener implements ActionListener {

    private TabPanel tabPanel;

    public CloseButtonListener(TabPanel tabPanel) {
        this.tabPanel = tabPanel;
        System.out.println("listener created");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("pressed");
    }
}
