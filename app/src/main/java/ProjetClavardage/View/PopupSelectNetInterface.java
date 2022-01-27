package ProjetClavardage.View;

import ProjetClavardage.Controller.MainController;
import ProjetClavardage.Model.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class PopupSelectNetInterface extends JFrame {
    private Pan parent;
    private MainController mc;

    public PopupSelectNetInterface(MainController mainController, Pan parent, Dimension dimensionParent) {
        super("Selectionnez l'interface reseau");
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
        JLabel label =new JLabel("Interface reseau /!\\ veuillez relancer l'application");
        pan.add(label, gbc);
        /*JTextField input = new JTextField();
        String placeholdermsg = "Entrez l'adresse IP";*/

        ArrayList<String> niNames = new ArrayList<>();
        ArrayList<String> niId = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = nis.nextElement();
                if (ni.isUp()) {
                    niNames.add(ni.toString());
                    niId.add(ni.getName());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        JComboBox input = new JComboBox(niNames.toArray());
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
                int index = input.getSelectedIndex();
                PopupSelectNetInterface.this.mc.setNi(niId.get(index));
                MainController.writeConfig(niId.get(index));
                PopupSelectNetInterface.this.dispose();
                System.out.println("selected ni : " + PopupSelectNetInterface.this.mc.getNi());
            }
        });

        this.add(pan);
        this.pack();
    }
}
