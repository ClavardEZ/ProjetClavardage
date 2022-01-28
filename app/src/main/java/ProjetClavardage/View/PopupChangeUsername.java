package ProjetClavardage.View;

import ProjetClavardage.Controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Gère l'affichage de la popup de changement de pseudonyme
 */
public class PopupChangeUsername extends JFrame {

    private Pan parent;
    private MainController mc;

    public PopupChangeUsername(MainController mainController, Pan parent, Dimension dimensionParent) {
        super("Ajouter une conversation");
        this.parent = parent;
        this.mc = mainController;

        this.setMinimumSize(new Dimension((int) (dimensionParent.getWidth()/4), (int) (dimensionParent.getHeight()/4)));
        Dimension windowSize = this.getSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point centerPoint = ge.getCenterPoint();
        int dx = centerPoint.x - windowSize.width / 2;
        int dy = centerPoint.y - windowSize.height / 2;
        this.setLocation(dx, dy);

        this.setVisible(true);
        JPanel pan = new JPanel();

        /* JPanel */
        pan.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 5, 10);
        JLabel label =new JLabel("Nouveau nom d'utilisateur : ");
        pan.add(label, gbc);
        JTextField input = new JTextField();
        String placeholdermsg = "Entrez l'adresse IP";
        input.addFocusListener(new TextPlaceholderListener(input, placeholdermsg));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        pan.add(input, gbc);

        JButton validate = new JButton("Valider");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 5, 10);
        pan.add(validate, gbc);
        validate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!input.getText().equals(placeholdermsg) && input.getText().length() > 3) {
                    if (PopupChangeUsername.this.mc.changeUserName(input.getText())) {
                    PopupChangeUsername.this.dispose();
                    }
                    else {
                        label.setText("Pseudonyme deja utlise !");
                    }
                } else {
                    label.setText("Pseudonyme trop court");
                }
            }
        });

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!input.getText().equals(placeholdermsg) && input.getText().length() > 3) {
                    if (PopupChangeUsername.this.mc.changeUserName(input.getText())) {
                        PopupChangeUsername.this.dispose();
                    }
                    else {
                        label.setText("Pseudonyme déjà utlisé !");
                    }
                } else {
                    label.setText("Pseudonyme trop court");
                }
            }
        });

        this.add(pan);
        this.pack();
    }
}
