package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupNewConv extends JFrame {

    private Pan parent;

    public PopupNewConv(Pan parent, Dimension dimensionParent) {
        super("Ajouter une conversation");
        this.parent = parent;

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
        pan.add(new JLabel("Adresse IP (temporaire pour test)"), gbc);
        JTextField input = new JTextField();
        String placeholdermsg = "Entrez l'adresse IP";
        input.addFocusListener(new TextPlaceholderListener(input, placeholdermsg));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        pan.add(input, gbc);

        JButton validate = new JButton("OK");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 5, 10);
        pan.add(validate, gbc);
        validate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!input.getText().equals(placeholdermsg)) {
                    PopupNewConv.this.parent.addContact(input.getText());
                    PopupNewConv.this.dispose();
                }
            }
        });

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!input.getText().equals(placeholdermsg)) {
                    PopupNewConv.this.parent.addContact(input.getText());
                    PopupNewConv.this.dispose();
                }
            }
        });

        //

        this.add(pan);
        this.pack();
        //this.setSize(new Dimension((int) (dimensionParent.getWidth()/4), (int) (dimensionParent.getHeight()/4)));
    }
}
