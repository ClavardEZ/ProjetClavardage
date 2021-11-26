package ProjetClavardage.View;

import ProjetClavardage.Controller.ControllerAddConversation;
import ProjetClavardage.Controller.ControllerContactList;
import ProjetClavardage.Model.MessageThreadManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Pan extends JPanel {

    private DefaultListModel<String> contacts;
    private JList<String[]> listeContacts;
    private JButton addConvButton;
    private MessageThreadManager msgManager;

    public Pan() {
        super();

        this.msgManager = new MessageThreadManager();
        this.msgManager.start();

        // loading resources
        BufferedImage logoutImage = null;
        BufferedImage settingsImage = null;
        BufferedImage userImage = null;
        BufferedImage sendImage = null;
        BufferedImage closeImage = null;
        try {
            logoutImage = ImageIO.read(this.getClass().getResource("/logout.png"));
            settingsImage = ImageIO.read(this.getClass().getResource("/settings.png"));
            userImage = ImageIO.read(this.getClass().getResource("/user.png"));
            sendImage = ImageIO.read(this.getClass().getResource("/send.png"));
            closeImage = ImageIO.read(this.getClass().getResource("/close.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        //sidePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));
        GridBagConstraints sideGbc = new GridBagConstraints();

        /* options panel */
        JPanel optionsPanel = new JPanel();
        sideGbc.gridx = 0;
        sideGbc.gridy = 0;
        sideGbc.weightx = 1.0;
        sideGbc.weighty = 1.5;
        sideGbc.ipadx = 15;
        sideGbc.ipady = 15;
        sideGbc.fill = GridBagConstraints.BOTH;
        sidePanel.add(optionsPanel, sideGbc);

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 100));
        JButton logout = new JButton(new ImageIcon(logoutImage));
        JButton settings = new JButton(new ImageIcon(settingsImage));
        JButton userPic = new JButton(new ImageIcon(userImage));
        userPic.setEnabled(false);

        optionsPanel.add(logout);
        optionsPanel.add(settings);
        optionsPanel.add(userPic);

        /* conversations panel */
        JPanel conversationsPanel = new JPanel();
        sideGbc.gridx = 0;
        sideGbc.gridy = 1;
        sideGbc.weightx = 1.0;
        sideGbc.weighty = 1.0;
        sideGbc.fill = GridBagConstraints.BOTH;
        sideGbc.insets = new Insets(0, 15, 0, 15);
        sideGbc.ipadx = 15;
        sideGbc.ipady = 15;
        sidePanel.add(conversationsPanel, sideGbc);
        conversationsPanel.setLayout(new BorderLayout());
        conversationsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30 ,0));
        this.contacts = new DefaultListModel<>();
        this.listeContacts = new JList(this.contacts);
        listeContacts.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listeContacts.setLayoutOrientation(JList.VERTICAL);
        listeContacts.setVisibleRowCount(12);
        listeContacts.setFont(new Font("Arial", Font.BOLD, 20));
        listeContacts.setFixedCellHeight(40);
        JScrollPane scrollPane = new JScrollPane(listeContacts);
        conversationsPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel conversationButtonsPanel = new JPanel();
        conversationsPanel.add(conversationButtonsPanel, BorderLayout.SOUTH);
        conversationButtonsPanel.setLayout(new BorderLayout());

        this.listeContacts.addMouseListener(new ControllerContactList(this));

        this.addConvButton = new JButton("+");
        conversationButtonsPanel.add(this.addConvButton, BorderLayout.EAST);
        ControllerAddConversation ctrlAddConv = new ControllerAddConversation(this, this.contacts);
        addConvButton.addActionListener(ctrlAddConv);

        conversationButtonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        /* message panel */
        JPanel messagePanel = new JPanel();
        mainGbc.gridx = 1;
        mainGbc.gridy = 0;
        mainGbc.weightx = 3.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        this.add(messagePanel, mainGbc);
        //messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        messagePanel.setLayout(new GridBagLayout());
        GridBagConstraints msgGbc = new GridBagConstraints();

        /* chat panel */
        JTabbedPane tabs = new JTabbedPane();
        msgGbc.gridx = 0;
        msgGbc.gridy = 0;
        msgGbc.weightx = 1.0;
        msgGbc.weighty = 20.0;
        msgGbc.fill = GridBagConstraints.BOTH;
        messagePanel.add(tabs, msgGbc);

        // JTabbed panes
        tabs.addTab("message 1", new JPanel());
        tabs.setTabComponentAt(0, new ButtonTabComponent(tabs, closeImage, 0));

        /* writing panel */
        JPanel writingPanel = new JPanel();
        msgGbc.gridx = 0;
        msgGbc.gridy = 1;
        msgGbc.weightx = 1.0;
        msgGbc.weighty = 1.0;
        msgGbc.fill = GridBagConstraints.HORIZONTAL;
        messagePanel.add(writingPanel, msgGbc);
        writingPanel.setLayout(new GridBagLayout());
        GridBagConstraints wrtGbc = new GridBagConstraints();

        String placeholderMessage = "Entrez votre message";
        JTextField textField = new JTextField(placeholderMessage);
        textField.setMargin(new Insets(0, 10, 0, 0));
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new TextPlaceholderListener(textField, placeholderMessage));

        wrtGbc.gridx = 0;
        wrtGbc.gridy = 0;
        wrtGbc.weightx = 20.0;
        wrtGbc.weighty = 1.0;
        wrtGbc.fill = GridBagConstraints.BOTH;
        wrtGbc.insets = new Insets(0, 5, 0, 5);
        wrtGbc.ipady = 15;
        wrtGbc.ipadx = 15;
        writingPanel.add(textField, wrtGbc);

        JButton sendButton = new JButton(new ImageIcon(sendImage));
        //sendButton.addActionListener(new CloseButtonListener());
        sendButton.setPreferredSize(new Dimension(30, 30));
        wrtGbc.gridx = 1;
        wrtGbc.gridy = 0;
        wrtGbc.weightx = 1.0;
        wrtGbc.weighty = 1.0;
        wrtGbc.fill = GridBagConstraints.BOTH;
        wrtGbc.insets = new Insets(0, 5, 0, 15);
        wrtGbc.ipady = 15;
        wrtGbc.ipadx = 0;
        writingPanel.add(sendButton, wrtGbc);
    }

    public void addContact(String username) {
        this.contacts.addElement(username);
        System.out.println("added" + this.contacts.toString());
    }

    public void openConversation(int index) {
        try {
            this.msgManager.openConnection(InetAddress.getByName(this.listeContacts.getModel().getElementAt(index)[0]));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
