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

public class MainController {
    // TODO link users lists with pan through this class
    // and not link other controllers to user, only through main class
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

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

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

            System.out.println("from config ni : " + this.ni);
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

    public String getUsernameByIP(InetAddress ip) {
        return this.userManager.getUserByIP(ip).getUsername();
    }

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

    public void removeUser(User user) {
        this.usersByUsername.remove(user.getUsername());
        this.usernameByusers.remove(user);
        this.pan.removeContact(user.getUsername());
        this.pan.revalidate();
    }

    //Ajoute l'utilisateur sélectonné dans la conv active
    //TODO Appeler cette fonction via un click particulier dans l'activity pannel
    public void addUserInConv(int index) {
        Conversation conv = this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex());
        //this.msgThdMngr.openConnection(this.usersByUsername.get(this.pan.getUsername(index)).getIP(), this.pan.getUsername(index),conv);
        //System.out.println("New user in conv, IP :  " + this.usersByUsername.get(this.pan.getUsername(index)).getIP());
        //DatabaseManager.addUserInConv();
        this.pan.revalidate();
    }

    public void openConversation(int index) {

        /*System.out.println("IP adress:" + InetAddress.getByName(this.pan.getUsername(index)));
        this.msgThdMngr.openConnection(InetAddress.getByName(this.pan.getUsername(index)));*/

        // TODO utiliser hashmap au lieu de index ? peut faire bugger

        System.out.println("mc [ENVOYEUR] open conversation");

        if (!this.tabByConv.containsKey(this.usersByUsername.get(this.pan.getUsername(index)).getIP())){  //evite la création de 2 tab avec meme destinataire

            System.out.println("mc [ENVOYEUR] open conversation : creation ok (pas de doublon de conv)");

            Conversation conv = new Conversation(this.pan.getUsername(index), msgThdMngr);
            InetAddress ip_address = this.usersByUsername.get(this.pan.getUsername(index)).getIP();

            this.tabIndexByAddress.add(ip_address);

            //this.msgThdMngr.openConnection(ip_address,conv);
            //this.msgThdMngr.openConnection(InetAddress.getLocalHost());

            //ChatPanel chatPanel = this.pan.addConversationTab(this.msgThdMngr.getConversationsAt(index).getName());

            // ajout à la base de données
            // si la conversation est déjà présente dans la base de données on charge les messages
            // ouverture depuis moi
            Conversation conv2 = DatabaseManager.getConvByIp(ip_address, this.msgThdMngr);
            if (conv2 != null) {
                System.out.println("mc [ENVOYEUR] openconversation : conv déjà dans la bdd");
                this.msgThdMngr.openConnection(ip_address,conv2);
                System.out.println("mc [ENVOYEUR] openconversation : openConnection succes");
                ChatPanel chatPanel = this.pan.addConversationTab(this.msgThdMngr.getConversationByIP(ip_address).getName());
                this.tabByConv.put(ip_address,chatPanel);
                //this.msgThdMngr.openConnection(InetAddress.getLocalHost());
                List<Message> messages = DatabaseManager.getAllMessagesFromConv(conv2, true, this.msgThdMngr);
                for (Message message :
                        messages) {
                    System.out.println("mc [ENVOYEUR] openConversation : message: " +
                            message.getContent() +
                            "; from " + message.getIP());
                    if (message.getIP() != null) {
                        if (message.getIP().equals(ip_address)) {
                            System.out.println("mc [ENVOYEUR] openConversation : add message from other");
                            this.addTextToTab(chatPanel, message.getUser().getUsername() + ">" + message.getContent());
                        } else {
                            this.pan.addTextToTabAsSender(message.getContent());
                        }
                    }
                }
            } else {
                // condition peut etre retiree ?
                System.out.println("mc [ENVOYEUR] open conversation : conv pas encore dans la bdd");
                DatabaseManager.addConversation(conv, ip_address);
                this.msgThdMngr.openConnection(ip_address,conv);
                ChatPanel chatPanel = this.pan.addConversationTab(this.msgThdMngr.getConversationByIP(ip_address).getName());
                this.tabByConv.put(ip_address,chatPanel);
                //this.msgThdMngr.openConnection(InetAddress.getLocalHost());
            }

            this.pan.revalidate();
        } else {
            System.out.println("mc [ENVOYEUR] open conversation : doublon de conv donc pas recree");
        }
    }

    public void closeConversation(int index) {
        System.out.println("mc [USER QUI FERME] close conversation");
        //this.msgThdMngr.close_conversation(index);
        InetAddress ip = this.usersByUsername.get(this.pan.getUsername(index)).getIP();
        this.msgThdMngr.close_conversation(ip);
        this.tabByConv.remove(ip);
        this.tabIndexByAddress.remove(ip);

        this.pan.revalidate();
    }

    public void sendMessage() {
        System.out.println("mc [ENVOYEUR MSG] send message");
        if (!this.pan.getTextfieldText().isBlank()) {
            this.pan.addTextToTabAsSender();
            //Message msg = new TextMessage(LocalDateTime.now(), this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
            Message msg = new TextMessage(LocalDateTime.now(), this.privateUser, this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
            this.msgThdMngr.send(msg, this.pan.getSelectedIndex());
            this.pan.emptyTextField();

            // ajout a la bd
            DatabaseManager.addMessage(msg);
        }
    }

    public void closingApp(){
        this.msgThdMngr.close_all_conversation();
        this.userManager.sender(false);
    }

    /*public void addConversationTab(String title) {
        this.pan.addConversationTab(title);
    }*/
    // reception de demande de conversation
    public Conversation addConversationTab(Conversation conv) {
        System.out.println("mc [RECEVEUR] addConversationTab");
        // TODO faire ajouter les messages reçus depuis la bd
        System.out.println("enter here");
        ChatPanel chatPanel = this.pan.addConversationTab(conv.getConvName());
        //DatabaseManager.addConversation();
        InetAddress ip_address = null;
        System.out.println("test nb users=" + conv.getUsersIP().size());
        if (conv.getUsersIP().size() > 0) {
            ip_address = conv.getUsersIP().get(0);
            this.tabByConv.put(ip_address,chatPanel);
            System.out.println("ip=" + ip_address);
            this.tabIndexByAddress.add(ip_address);
            // si la conversation est déjà dans la base de données
            Conversation conv2 = DatabaseManager.getConvByIp(ip_address, this.msgThdMngr);
            if (conv2 != null) {
                System.out.println("mc [RECEVEUR] addConversationTab : conv cree car pas doublon");
                Conversation.copyUsers(conv, conv2);
                List<Message> messages = DatabaseManager.getAllMessagesFromConv(conv2, true, this.msgThdMngr);
                for (Message message :
                        messages) {
                    System.out.println("mc [RECEVEUR] openConversation : message: " +
                            message.getContent() +
                            "; from " + message.getIP());
                    if (message.getIP().equals(ip_address)) {
                        //this.addTextToTab(conv, message.getUser().getUsername() + ">" + message.getContent());
                        System.out.println("mc [RECEVEUR] openConversation : add message from other");
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

    public boolean changeUserName(String username) { //renvoie 0 si erreur
        System.out.println("mc [USER QUI CHANGE] change username");
        boolean bool = this.privateUser.updateUsername(username);
        if (bool){
            this.userManager.sender(true);
            this.updateChatPanel(this.privateUser);
        }
        this.pan.revalidate();
        return bool;
    }

    public boolean changeUserName(User user, String oldUsername, String newUsername) {
        if (!oldUsername.equals(newUsername)) {
            System.out.println("mc [RECEVEUR CHANGE USERNAME] changeUserName");
            this.usersByUsername.get(user.getUsername()).setUsername(newUsername);
            this.usersByUsername.put(newUsername, user);
            this.usersByUsername.remove(oldUsername);
            DatabaseManager.changeUsername(user.getIP(), newUsername);
            this.updateChatPanel(user);

            //this.pan.setUsername(oldUsername, newUsername);
        }

        return true;
    }

    public void addContact(String username) {
        this.pan.addContact(username);
    }

    public void removeConversationTab(int index) {
        System.out.println("removeConvTab 1");
        this.pan.removeConversationTab(index);
    }

    public void removeConversationTab(Conversation conv) {
        System.out.println("removeConvTab 2");
        this.pan.removeConversationTab(this.tabIndexByAddress.indexOf(conv.getFirstIP()));

        this.tabByConv.remove(conv.getFirstIP());
        this.tabIndexByAddress.remove(conv.getFirstIP());
    }

    /*public void addTextToTab(int index, String text) {
        this.pan.addTextToTab(index, text);
    }*/

    public void addTextToTab(Conversation conv, String text) {
        //this.pan.addTextToTab(tabByConv.get(conv), text);
        if (conv.getUsersIP().size() > 0) {
            this.pan.addTextToTab(this.tabByConv.get(conv.getUsersIP().get(0)), text);
        }
    }

    public void addTextToTab(ChatPanel chatPanel, String text) {
        //this.pan.addTextToTab(tabByConv.get(conv), text);
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
