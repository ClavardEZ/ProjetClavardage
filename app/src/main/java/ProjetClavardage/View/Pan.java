package ProjetClavardage.View;

import ProjetClavardage.Controller.*;
import ProjetClavardage.Model.Conversation;
import ProjetClavardage.Model.Message;
/*import ProjetClavardage.Model.Message;
import ProjetClavardage.Model.MessageThreadManager;
import ProjetClavardage.Model.TextMessage;*/

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe principale de la vue
 */
public class Pan extends JPanel {

    private DefaultListModel<String> contacts;
    private JList<String> listeContacts;
    private JButton addConvButton;
    private JTabbedPane tabs;
    private JButton sendButton;
    private JTextField textField;
    private ArrayList<ChatPanel> chatPanels;
    private MainController mc;
    private TextPlaceholderListener txtLstner;

    private BufferedImage closeImage;

    public Pan(MainController mc) {
        super();

        this.mc = mc;
        this.chatPanels = new ArrayList<>();

        // loading resources
        BufferedImage logoutImage = null;
        BufferedImage settingsImage = null;
        BufferedImage userImage = null;
        BufferedImage sendImage = null;
        this.closeImage = null;
        try {
            logoutImage = ImageIO.read(this.getClass().getResource("/logout.png"));
            settingsImage = ImageIO.read(this.getClass().getResource("/settings.png"));
            userImage = ImageIO.read(this.getClass().getResource("/user.png"));
            sendImage = ImageIO.read(this.getClass().getResource("/send.png"));
            this.closeImage = ImageIO.read(this.getClass().getResource("/close.png"));
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
        JButton search = new JButton("Search message");
        search.addActionListener(new ControllerSearchMessage(this, this.mc));
        userPic.setEnabled(false);

        optionsPanel.add(logout);
        optionsPanel.add(settings);
        optionsPanel.add(userPic);
        optionsPanel.add(search);

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
        listeContacts.setFont(new Font("Arial", Font.BOLD, 15));
        listeContacts.setFixedCellHeight(40);
        listeContacts.setPrototypeCellValue("                    ");

        JScrollPane scrollPane = new JScrollPane(listeContacts);
        conversationsPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel conversationButtonsPanel = new JPanel();
        conversationsPanel.add(conversationButtonsPanel, BorderLayout.SOUTH);
        conversationButtonsPanel.setLayout(new BorderLayout());

        this.listeContacts.addMouseListener(new ControllerContactList(this.mc));

        this.addConvButton = new JButton("+");
        conversationButtonsPanel.add(this.addConvButton, BorderLayout.EAST);
        ControllerAddConversation ctrlAddConv = new ControllerAddConversation(this.mc,this, this.contacts);
        addConvButton.addActionListener(ctrlAddConv);
        settings.addActionListener(ctrlAddConv);

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
        this.tabs = new JTabbedPane();
        msgGbc.gridx = 0;
        msgGbc.gridy = 0;
        msgGbc.weightx = 1.0;
        msgGbc.weighty = 20.0;
        msgGbc.fill = GridBagConstraints.BOTH;
        messagePanel.add(this.tabs, msgGbc);

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
        this.textField = new JTextField(placeholderMessage);
        this.textField.setMargin(new Insets(0, 10, 0, 0));
        this.textField.setForeground(Color.GRAY);
        this.txtLstner = new TextPlaceholderListener(this.textField, placeholderMessage);
        this.textField.addFocusListener(txtLstner);
        /*this.textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Pan.this.sendMessage();
            }
        });*/
        this.textField.addActionListener(new ControllerSendMessage(this, this.mc));

        wrtGbc.gridx = 0;
        wrtGbc.gridy = 0;
        wrtGbc.weightx = 20.0;
        wrtGbc.weighty = 1.0;
        wrtGbc.fill = GridBagConstraints.BOTH;
        wrtGbc.insets = new Insets(0, 5, 0, 5);
        wrtGbc.ipady = 15;
        wrtGbc.ipadx = 15;
        writingPanel.add(this.textField, wrtGbc);

        this.sendButton = new JButton(new ImageIcon(sendImage));
        this.sendButton.addActionListener(new ControllerSendMessage(this, this.mc));
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
    }

    public void removeContact(String username) {
        System.out.println("removed user : " + username);
        this.contacts.removeElement(username);
    }

    public ChatPanel addConversationTab(String title) {
        this.chatPanels.add(new ChatPanel(this));
        tabs.addTab(title, this.chatPanels.get(this.chatPanels.size() - 1));
        tabs.setTabComponentAt(this.tabs.getTabCount() - 1, new ButtonTabComponent(this.mc, tabs, this.closeImage, this.tabs.getTabCount() - 1, this));
        return this.chatPanels.get(this.chatPanels.size() - 1);
        // TODO add reveived conv to contacts tab
    }

    public void removeConversationTab(int index) {
        this.tabs.remove(index);
    }

    public void addTextToTab(ChatPanel chatPanel, String text) {
        // TODO refactor to not use index  ::::Cbon
        this.revalidate();
        //ChatPanel chatPanel = (ChatPanel) this.tabs.getComponentAt(tabIndex);
        chatPanel.addText(text, false);
    }

    public void addTextToTabAsSender() {
        this.revalidate();
        int tabIndex = this.tabs.getSelectedIndex();
        ChatPanel chatPanel = (ChatPanel) this.tabs.getComponentAt(tabIndex);
        chatPanel.addText( this.mc.getPrivateUsername() + ">" + this.textField.getText(), true);
    }

    public void addTextToTabAsSender(String text) {
        this.revalidate();
        int tabIndex = this.tabs.getSelectedIndex();
        ChatPanel chatPanel = (ChatPanel) this.tabs.getComponentAt(tabIndex);
        chatPanel.addText( this.mc.getPrivateUsername() + ">" + text, true);
    }

    public void addTextToTabAsSender(ChatPanel chatPanel, String text) {
        this.revalidate();
        chatPanel.addText(this.mc.getPrivateUsername() + ">" + text, true);
    }

    public int getSelectedIndex() {
        /*if (this.tabs.getSelectedIndex()<0) {
            return 0;
        }*/
        return this.tabs.getSelectedIndex();
    }

    public String getTextfieldText() {
        return this.textField.getText();
    }

    public void emptyTextField() {
        this.textField.setText("");
    }

    public String getUsername(int index) {
        return this.contacts.get(index);
    }

    public boolean isPlaceholderText() {
        return this.txtLstner.isPlaceholder();
    }
}