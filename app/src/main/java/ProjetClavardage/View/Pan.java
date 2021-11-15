package ProjetClavardage.View;

import javax.swing.*;
import javax.swing.plaf.TabbedPaneUI;
import javax.tools.Tool;
import java.awt.*;

public class Pan extends JPanel {

    public Pan() {
        super();

        /* main panel */
        this.setLayout(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();

        /* side panel */
        JPanel sidePanel = new JPanel();
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        this.add(sidePanel, mainGbc);

        sidePanel.setLayout(new GridBagLayout());
        GridBagConstraints sideGbc = new GridBagConstraints();

        /* options panel */
        JPanel optionsPanel = new JPanel();
        sideGbc.gridx = 0;
        sideGbc.gridy = 0;
        sideGbc.weightx = 1.0;
        sideGbc.weighty = 1.0;
        sideGbc.fill = GridBagConstraints.BOTH;
        sidePanel.add(optionsPanel, sideGbc);

        optionsPanel.setLayout(new BorderLayout(0, 0));
        optionsPanel.add(new JButton("Options panel"));

        /* conversations panel */
        JPanel conversationsPanel = new JPanel();
        sideGbc.gridx = 0;
        sideGbc.gridy = 1;
        sideGbc.weightx = 1.0;
        sideGbc.weighty = 1.0;
        sideGbc.fill = GridBagConstraints.BOTH;
        sidePanel.add(conversationsPanel, sideGbc);

        conversationsPanel.setLayout(new BorderLayout());
        String[] contacts = {"Camille Wagner",
                "Renee de Guyon",
                "Christine Begue",
                "Jules Lesage",
                "Noel Lebrun",
                "William du Guillon",
                "Xavier-Jacques Courtois",
                "Victor de Morvan",
                "Antoine Vincent-Rousset",
                "Lucy-Michelle Teixeira",};
        JList<String[]> listeContacts = new JList(contacts);
        listeContacts.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listeContacts.setLayoutOrientation(JList.VERTICAL);
        listeContacts.setVisibleRowCount(-1);
        listeContacts.setFont(new Font("Arial", Font.BOLD, 20));
        listeContacts.setFixedCellHeight(50);

        conversationsPanel.add(listeContacts);

        /* message panel */
        JPanel messagePanel = new JPanel();
        mainGbc.gridx = 1;
        mainGbc.gridy = 0;
        mainGbc.weightx = 3.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        this.add(messagePanel, mainGbc);

        messagePanel.setLayout(new BorderLayout());

        /* writing panel */
        JPanel writingPanel = new JPanel();
        messagePanel.add(writingPanel, BorderLayout.SOUTH);
        writingPanel.setLayout(new BorderLayout());

        JTextField textField = new JTextField("Message");
        writingPanel.add(textField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        writingPanel.add(sendButton, BorderLayout.EAST);

        /* chat panel */
        JTabbedPane chatPanel = new JTabbedPane();
        messagePanel.add(chatPanel, BorderLayout.CENTER);

        // placeholder panels
        JPanel[] panels = new JPanel[5];
        for (int i = 0; i < 5; i++) {
            panels[i] = new JPanel();
            chatPanel.addTab("Onglet " + i, panels[i]);
        }
    }
}
