package ProjetClavardage.View;

import ProjetClavardage.Controller.MainController;
import ProjetClavardage.Model.Conversation;
import ProjetClavardage.Model.DatabaseManager;
import ProjetClavardage.Model.Message;
import com.sun.tools.javac.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.UUID;

/**
 * Gère l'affichage de la popup de recherche de messages
 */
public class PopupSearchMessage extends JFrame {

    private Pan parent;
    private MainController mc;

    public PopupSearchMessage(Pan parent, MainController mc, Dimension dimensionParent) {
        super("Ajouter une conversation");
        this.parent = parent;
        this.mc = mc;

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
        JLabel label =new JLabel("Recherche de message : ");
        pan.add(label, gbc);
        JTextField input = new JTextField();
        String placeholdermsg = "Entrez le message";
        input.addFocusListener(new TextPlaceholderListener(input, placeholdermsg));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        pan.add(input, gbc);

        JButton validate = new JButton("Chercher un message");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 5, 10);
        pan.add(validate, gbc);
        validate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PopupSearchMessage.this.search(input.getText());
                PopupSearchMessage.this.dispose();
            }
        });

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PopupSearchMessage.this.search(input.getText());
                PopupSearchMessage.this.dispose();
            }
        });

        //

        this.add(pan);
        this.pack();
    }

    public void search(String text) {
        Conversation conv = this.mc.getMsgThdMngr().getConversationsAt(this.parent.getSelectedIndex());
        Conversation realConv = null;
        if (conv.getUsersIP().size() > 0) {
            realConv = DatabaseManager.getConvByIp(conv.getUsersIP().get(0), this.mc.getMsgThdMngr());
        }
        List<Message> messages = DatabaseManager.searchMessageBytext(realConv,
                text,
                this.mc.getMsgThdMngr());

        new PopupSearchResult(messages);
    }
}
