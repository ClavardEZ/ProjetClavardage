package ProjetClavardage.View;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;

public class Pan extends JPanel {

    public Pan() {
        super();

        // side panel
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(1, 1));
        sidePanel.add(new JButton("Test side panel"));

        // message panel
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(1, 1));
        messagePanel.add(new JButton("Test message panel"));

        // main panel
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(sidePanel, BorderLayout.WEST);
        this.add(messagePanel, BorderLayout.CENTER);
    }
}
