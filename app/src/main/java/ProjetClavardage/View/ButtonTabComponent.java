package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ButtonTabComponent extends JPanel {
    private JTabbedPane pane;
    private Pan parentPane;

    public ButtonTabComponent(JTabbedPane pane, BufferedImage image, int i, Pan parentPane) {
        this.pane = pane;
        this.parentPane = parentPane;
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        String text = pane.getTitleAt(i);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 5.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(new JLabel(text), gbc);
        Image img = image;
        ImageIcon icon = new ImageIcon(img.getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        TabButton closeButton = new TabButton(icon);
        //closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(closeButton, gbc);
    }

    private class TabButton extends JButton implements ActionListener {

        public TabButton(ImageIcon icon) {
            super(icon);
            this.addActionListener(this);
            this.setPreferredSize(new Dimension(17, 17));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
                System.out.println("valeur de i=" + i);
                pane.remove(i);
                parentPane.closeConversation(i);
            }
        }
    }
}
