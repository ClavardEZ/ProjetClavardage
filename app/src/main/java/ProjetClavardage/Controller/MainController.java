package ProjetClavardage.Controller;

import ProjetClavardage.Model.*;
import ProjetClavardage.View.ButtonTabComponent;
import ProjetClavardage.View.ChatPanel;
import ProjetClavardage.View.Pan;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.checkerframework.checker.units.qual.A;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Gère les éntrées utilisateur
 */
public class MainController {
    private ButtonTabComponent btnTabComponent;
    private Pan pan;
    private MessageThreadManager msgThdMngr;
    private PrivateUser privateUser;
    private UserManager userManager;
    private HashMap<InetAddress, ChatPanel> tabByConv;
    private HashMap<String, User> usersByUsername;
    private HashMap<User, String> usernameByusers;
    private ArrayList<InetAddress> tabIndexByAddress;

    private String ni;

    /**
     *Crée la base de donnée, lances les serveurs TCP et UDP et crée les éventuels fichiers locaux,
     * @param serverPort port du serveur TCP
     * @param clientPort port TCP des autres utilisateurs (éventuellement différent du port serveur pour tests en local)
     * @param listeningPort port du serveur UDP
     * @param sendingPort port UDP des autres utilisateurs (éventuellement différent du port serveur pour tests en local)
     * @param username nom de l'utilisateur courant
     */
    public MainController(int serverPort, int clientPort, int listeningPort, int sendingPort, String username) {
        DatabaseManager.connect();
        DatabaseManager.createTables();

        AppDirs appDirs = AppDirsFactory.getInstance();
        String dataFolder = appDirs.getUserDataDir("ClavardEZ", null, "Clavardeurs");
        (new File(dataFolder)).mkdirs();

        String url = dataFolder + File.separator + "config.properties";

        File configFile = new File(url);

        FileReader reader = null;
        try {
            if (!configFile.exists()) {
                MainController.writeConfig("lo");
            }
            reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);
            this.ni = props.getProperty("ni");
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.pan = new Pan(this);
        this.msgThdMngr = new MessageThreadManager(this, serverPort, clientPort);
        this.msgThdMngr.start();

        if (this.ni == null || this.ni == "") {
            this.privateUser = new PrivateUser(MessageThreadManager.getLocalAddress("lo"), username); // set correct ip address
        } else {
            this.privateUser = new PrivateUser(MessageThreadManager.getLocalAddress(this.ni), username); // set correct ip address
        }

        // udp
        this.usernameByusers = new HashMap<>();
        this.usersByUsername = new HashMap<>();
        this.tabByConv = new HashMap<>();
        this.tabIndexByAddress = new ArrayList<>();

        this.userManager = new UserManager(this, this.privateUser, listeningPort, sendingPort);
        this.userManager.start_listener();
        this.userManager.start();
        this.userManager.sender(true);

        PrivateUser dbUser = DatabaseManager.getPrivateUser();
        if (dbUser == null) {
            DatabaseManager.addPrivateUser(this.privateUser);
        } else {
            this.privateUser.setUsername(dbUser.getUsername());
        }
    }

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

    public String getUsernameByIP(InetAddress ip) {
        return this.userManager.getUserByIP(ip).getUsername();
    }

    /**
     * Crée une instance de user, met éventuellement a jour son pseudo
     * @param user
     */
    public void addUser(User user) {
        if (!this.usersByUsername.containsKey(user.getUsername())) {
            if (this.usernameByusers.containsKey(user)) {
                this.pan.removeContact(usernameByusers.get(user));
                this.usersByUsername.remove(usernameByusers.get(user));
                this.usernameByusers.remove(user);
            }
            this.usersByUsername.put(user.getUsername(), user);
            this.usernameByusers.put(user, user.getUsername());
            // ajout à a vue
            // can be called directly
            this.addContact(user.getUsername());
            // ajout dans la bd
            if (DatabaseManager.getUser(user.getIP()) == null) {
                DatabaseManager.addUser(user);
            }
        }
        this.pan.revalidate();
    }

    /**
     * Supprime l'user de l'affichage
     * @param user
     */
    public void removeUser(User user) {
        this.usersByUsername.remove(user.getUsername());
        this.usernameByusers.remove(user);
        this.pan.removeContact(user.getUsername());
        this.pan.revalidate();
    }

    @Deprecated
    public void addUserInConv(int index) {
        Conversation conv = this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex());
        this.pan.revalidate();
    }

    /**
     * Initie une conversation en vérifiant si une conversaton avec cet utilisateur a déjà eu lieu dans le passé. Si tel est le cas, on se sert de la base de donnée pour afficher l'historique
     * @param index index de l'utilisateur avec lequel on crée la conversation
     */
    public void openConversation(int index) {
        if (!this.tabByConv.containsKey(this.usersByUsername.get(this.pan.getUsername(index)).getIP())){  //evite la création de 2 tab avec meme destinataire

            Conversation conv = new Conversation(this.pan.getUsername(index), msgThdMngr);
            InetAddress ip_address = this.usersByUsername.get(this.pan.getUsername(index)).getIP();

            this.tabIndexByAddress.add(ip_address);
            // ajout à la base de données
            // si la conversation est déjà présente dans la base de données on charge les messages
            // ouverture depuis moi
            Conversation conv2 = DatabaseManager.getConvByIp(ip_address, this.msgThdMngr);
            if (conv2 != null) {
                this.msgThdMngr.openConnection(ip_address,conv2);
                ChatPanel chatPanel = this.pan.addConversationTab(this.msgThdMngr.getConversationByIP(ip_address).getName());
                this.tabByConv.put(ip_address,chatPanel);
                //this.msgThdMngr.openConnection(InetAddress.getLocalHost());
                List<Message> messages = DatabaseManager.getAllMessagesFromConv(conv2, true, this.msgThdMngr);
                for (Message message :
                        messages) {
                    if (message.getIP() != null) {
                        if (message.getIP().equals(ip_address)) {
                            this.addTextToTab(chatPanel, message.getUser().getUsername() + ">" + message.getContent());
                        } else {
                            this.pan.addTextToTabAsSender(message.getContent());
                        }
                    }
                }
            } else {
                DatabaseManager.addConversation(conv, ip_address);
                this.msgThdMngr.openConnection(ip_address,conv);
                ChatPanel chatPanel = this.pan.addConversationTab(this.msgThdMngr.getConversationByIP(ip_address).getName());
                this.tabByConv.put(ip_address,chatPanel);
            }

            this.pan.revalidate();
        }
    }

    /**
     * Ferme la conversation
     * @param index
     */
    public void closeConversation(int index) {
        InetAddress ip = this.usersByUsername.get(this.pan.getUsername(index)).getIP();
        this.msgThdMngr.close_conversation(ip);
        this.tabByConv.remove(ip);
        this.tabIndexByAddress.remove(ip);

        this.pan.revalidate();
    }

    /**
     * envoi un message dans la conversation courante
     */
    public void sendMessage() {
        if (!this.pan.getTextfieldText().isBlank()) {
            this.pan.addTextToTabAsSender();
            Message msg = new TextMessage(LocalDateTime.now(), this.privateUser, this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
            this.msgThdMngr.send(msg, this.pan.getSelectedIndex());
            this.pan.emptyTextField();

            // ajout a la bd
            DatabaseManager.addMessage(msg);
        }
    }

    /**
     * Fermes les conversations en cours et envoie un message de déconnexion
     */
    public void closingApp(){
        this.msgThdMngr.close_all_conversation();
        this.userManager.sender(false);
    }

    /**
     * Ajout d'un onglet de conversation en cas d'une réception de demande de connexion
     * @param conv
     * @return
     */
    public Conversation addConversationTab(Conversation conv) {
        ChatPanel chatPanel = this.pan.addConversationTab(conv.getConvName());
        InetAddress ip_address = null;
        if (conv.getUsersIP().size() > 0) {
            ip_address = conv.getUsersIP().get(0);
            this.tabByConv.put(ip_address,chatPanel);
            this.tabIndexByAddress.add(ip_address);
            // si la conversation est déjà dans la base de données
            Conversation conv2 = DatabaseManager.getConvByIp(ip_address, this.msgThdMngr);
            if (conv2 != null) {
                Conversation.copyUsers(conv, conv2);
                List<Message> messages = DatabaseManager.getAllMessagesFromConv(conv2, true, this.msgThdMngr);
                for (Message message :
                        messages) {
                    if (message.getIP().equals(ip_address)) {
                        this.addTextToTab(chatPanel, message.getUser().getUsername() + ">" + message.getContent());
                    } else {
                        this.pan.addTextToTabAsSender(chatPanel, message.getContent());
                    }
                }
                return conv2;
            } else {
                DatabaseManager.addConversation(conv, ip_address);
            }
        }
        this.pan.revalidate();
        return conv;
    }

    /**
     * Change le pseudonyme de l'utilisateur courant
     * @param username nouveau pseudo
     * @return renvoie faux si le pseudo est déjà pris, vrai sinon
     */
    public boolean changeUserName(String username) {
        boolean bool = this.privateUser.updateUsername(username);
        if (bool){
            this.userManager.sender(true);
            this.updateChatPanel(this.privateUser);
        }
        this.pan.revalidate();
        return bool;
    }

    /**
     * Change le pseudonyme d'un utilisateur distant
     * @param user
     * @param oldUsername
     * @param newUsername
     * @return
     */
    public boolean changeUserName(User user, String oldUsername, String newUsername) {
        if (!oldUsername.equals(newUsername)) {
            this.usersByUsername.get(user.getUsername()).setUsername(newUsername);
            this.usersByUsername.put(newUsername, user);
            this.usersByUsername.remove(oldUsername);
            DatabaseManager.changeUsername(user.getIP(), newUsername);
            this.updateChatPanel(user);
        }
        return true;
    }

    /**
     * Ajoute un contact dans le panneau de contacts
     * @param username
     */
    public void addContact(String username) {
        this.pan.addContact(username);
    }

    /**
     * Supprime un onglet de conversation
     * @param index
     */
    public void removeConversationTab(int index) {
        this.pan.removeConversationTab(index);
    }

    /**
     * Supprime un onglet de conversation
     * @param conv
     */
    public void removeConversationTab(Conversation conv) {
        this.pan.removeConversationTab(this.tabIndexByAddress.indexOf(conv.getFirstIP()));

        this.tabByConv.remove(conv.getFirstIP());
        this.tabIndexByAddress.remove(conv.getFirstIP());
    }

    /**
     * Ajoute du texte dans une conversation
     * @param conv
     * @param text
     */
    public void addTextToTab(Conversation conv, String text) {
        if (conv.getUsersIP().size() > 0) {
            this.pan.addTextToTab(this.tabByConv.get(conv.getUsersIP().get(0)), text);
        }
    }

    public void addTextToTab(ChatPanel chatPanel, String text) {
        this.pan.addTextToTab(chatPanel, text);
    }

    public HashMap getTabByConv() {return this.tabByConv;}

    public Pan getPan() {
        return pan;
    }

    public MessageThreadManager getMsgThdMngr() {
        return msgThdMngr;
    }

    public String getPrivateUsername() {
        return this.privateUser.getUsername();
    }

    public boolean isPlaceholderText() {
        return this.pan.isPlaceholderText();
    }

    /**
     * met a jour les pseudonymes dans le fil de discussion avec user
     * @param user
     */
    public void updateChatPanel(User user) {
        ChatPanel chatPanel = null;
        if (user.getIP().equals(this.privateUser.getIP())) {
            for (InetAddress ipAddress :
                 this.tabByConv.keySet()) {
                chatPanel = this.tabByConv.get(ipAddress);
                chatPanel.clearText();
                ArrayList<Message> messages = new ArrayList<>(DatabaseManager.getAllMessagesFromConv(DatabaseManager.getConvByIp(ipAddress, this.msgThdMngr),
                        true,
                        this.msgThdMngr));
                for (Message message :
                        messages) {
                    if (message.getIP().equals(user.getIP())) {
                        this.pan.addTextToTabAsSender(chatPanel, message.getContent());
                    } else {
                        this.addTextToTab(chatPanel, message.getUser().getUsername() + ">" + message.getContent());
                    }
                }
            }
        } else {
            chatPanel = this.tabByConv.get(user.getIP());
            if (chatPanel != null) {
                chatPanel.clearText();
                ArrayList<Message> messages = new ArrayList<>(DatabaseManager.getAllMessagesFromConv(DatabaseManager.getConvByIp(user.getIP(), this.msgThdMngr),
                        true,
                        this.msgThdMngr));
                for (Message message :
                        messages) {
                    if (message.getIP().equals(user.getIP())) {
                        this.addTextToTab(chatPanel, message.getUser().getUsername() + ">" + message.getContent());
                    } else {
                        this.pan.addTextToTabAsSender(chatPanel, message.getContent());
                    }
                }
            }
        }
    }

    public void refreshUI() {
        this.pan.revalidate();
    }

    public InetAddress getPrivateUserIp() {
        return this.privateUser.getIP();
    }

    /**
     * Change dans les paramètres l'nterface réseau sélectionnée
     * @param ni
     */
    public static void writeConfig(String ni) {
        DatabaseManager.changePrivateIp(MessageThreadManager.getLocalAddress(ni));

        AppDirs appDirs = AppDirsFactory.getInstance();
        String dataFolder = appDirs.getUserDataDir("ClavardEZ", null, "Clavardeurs");
        (new File(dataFolder)).mkdirs();

        String url = dataFolder + File.separator + "config.properties";

        File configFile = new File(url);

        Properties props = new Properties();
        props.setProperty("ni", ni);
        try {
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "lo");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
