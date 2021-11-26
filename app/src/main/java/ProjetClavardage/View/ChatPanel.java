package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;

public class ChatPanel extends JPanel {

    private Pan parent;
    private JLabel text;

    public ChatPanel(Pan parent) {
        super();
        this.parent = parent;
        this.text = new JLabel("TEXT");
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;

        this.add(this.text, gbc);
    }

    public void addText(String text) {
        this.text.setText(text);
    }
}
