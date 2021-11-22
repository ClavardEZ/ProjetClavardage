package ProjetClavardage.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TabPanel extends JPanel {
    private JButton closeButton;
    private JTabbedPane parent;

    public TabPanel(String title, JTabbedPane parent) {
        super();
        this.parent = parent;
        JLabel titleLabel = new JLabel(title);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        BufferedImage closeImage = null;
        try {
            closeImage = ImageIO.read(this.getClass().getResource("/delete.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.closeButton = new JButton(new ImageIcon(closeImage.getScaledInstance(10, 10, Image.SCALE_SMOOTH)));
        this.closeButton.setMargin(new Insets(0, 0, 0, 0));
        //this.closeButton.addActionListener(new CloseButtonListener());
        this.closeButton.setEnabled(true);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 1.0;
        gbc.weightx = 5.0;
        this.add(titleLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(2, 10, 0, 0);
        this.add(this.closeButton, gbc);
    }

    public void actionPerformed(ActionEvent e) {
        int i = this.parent.indexOfTabComponent(TabPanel.this);
        if (i != -1) {
            this.parent.remove(i);
        }
    }

    @Override
    public JTabbedPane getParent() {
        return parent;
    }
}
