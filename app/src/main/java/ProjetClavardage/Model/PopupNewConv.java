package ProjetClavardage.Model;

import ProjetClavardage.View.Pan;
import ProjetClavardage.View.TextPlaceholderListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PopupNewConv extends JFrame {

    private Pan parent;

    public PopupNewConv(Pan parent, Dimension dimensionParent) {
        super("Ajouter une conversation");
        this.parent = parent;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        pan.setLayout(new BorderLayout());
        pan.add(new JLabel("Adresse IP (temporaire pour test)"), BorderLayout.NORTH);
        JTextField input = new JTextField();
        String placeholdermsg = "Entrez l'adresse IP";
        input.addFocusListener(new TextPlaceholderListener(input, placeholdermsg));
        pan.add(input, BorderLayout.CENTER);

        JButton validate = new JButton("OK");
        pan.add(validate, BorderLayout.SOUTH);
        validate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO do not add if placeholder
                PopupNewConv.this.parent.addContact(input.getText());
                PopupNewConv.this.dispose();
            }
        });

        //

        this.add(pan);
        this.pack();
        //this.setSize(new Dimension((int) (dimensionParent.getWidth()/4), (int) (dimensionParent.getHeight()/4)));
    }
}
