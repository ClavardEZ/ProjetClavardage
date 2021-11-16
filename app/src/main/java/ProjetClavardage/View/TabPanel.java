package ProjetClavardage.View;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TabPanel extends JPanel {
    private JButton closeButton;

    public TabPanel(String title) {
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
        //this.closeButton.setMaximumSize(new Dimension(10, 10));
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
}
