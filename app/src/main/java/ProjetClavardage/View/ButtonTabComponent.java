package ProjetClavardage.View;

import ProjetClavardage.Controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class ButtonTabComponent extends JPanel {
    private JTabbedPane pane;
    private Pan parentPane;
    private MainController mc;
    private JLabel textLabel;

    public ButtonTabComponent(MainController mc, JTabbedPane pane, BufferedImage image, int i, Pan parentPane) {
        this.mc = mc;
        this.pane = pane;
        this.parentPane = parentPane;
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        String text = pane.getTitleAt(i);
        this.textLabel = new JLabel(text);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 5.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.textLabel, gbc);
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

    // TODO maybe external class ???
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
                mc.closeConversation(i);
            }
        }
    }
}
