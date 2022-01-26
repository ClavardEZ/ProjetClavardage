package ProjetClavardage.Controller;

import ProjetClavardage.Model.*;
import ProjetClavardage.View.ButtonTabComponent;
import ProjetClavardage.View.ChatPanel;
import ProjetClavardage.View.Pan;

import javax.xml.crypto.Data;
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

    public static String ni;

    public MainController(int serverPort, int clientPort, int listeningPort, int sendingPort, String username, String ni) {
        this.pan = new Pan(this);
        this.msgThdMngr = new MessageThreadManager(this, serverPort, clientPort);
        this.msgThdMngr.start();

        this.ni = ni;

        System.out.println("string ni : " + ni);

        try {
            Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni2 = nis.nextElement();
                if (ni2.isUp()) {
                    System.out.println("interface : " + ni2.toString());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.privateUser = new PrivateUser(MessageThreadManager.getLocalAddress(ni), username); // set correct ip address
        System.out.println("private user ip : " + this.privateUser.getIP().getHostAddress());

        // udp
        this.usernameByusers = new HashMap<>();
        this.usersByUsername = new HashMap<>();
        this.tabByConv = new HashMap<>();

        this.userManager = new UserManager(this, this.privateUser, listeningPort, sendingPort);
        this.userManager.start_listener();
        this.userManager.start();
        this.userManager.sender(true);

        DatabaseManager.connect();
        DatabaseManager.createTables();

        if (DatabaseManager.getPrivateUser() == null) {
            DatabaseManager.addPrivateUser(this.privateUser);
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
                System.out.println("user added to database");
                DatabaseManager.addUser(user);
            }
        }
    }

    public void removeUser(User user) {
        System.out.println("user removed from mc");
        this.usersByUsername.remove(user.getUsername());
        this.usernameByusers.remove(user);
        this.pan.removeContact(user.getUsername());
    }

    //Ajoute l'utilisateur sélectonné dans la conv active
    //TODO Appeler cette fonction via un click particulier dans l'activity pannel
    public void addUserInConv(int index) {
        Conversation conv = this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex());
        //this.msgThdMngr.openConnection(this.usersByUsername.get(this.pan.getUsername(index)).getIP(), this.pan.getUsername(index),conv);
        //System.out.println("New user in conv, IP :  " + this.usersByUsername.get(this.pan.getUsername(index)).getIP());
        //DatabaseManager.addUserInConv();
    }

    public void openConversation(int index) {

        /*System.out.println("IP adress:" + InetAddress.getByName(this.pan.getUsername(index)));
        this.msgThdMngr.openConnection(InetAddress.getByName(this.pan.getUsername(index)));*/

        // TODO utiliser hashmap au lieu de index ? peut faire bugger

        if (!this.tabByConv.containsKey(this.pan.getUsername(index))){  //evite la création de 2 tab avec meme destinataire


            Conversation conv = new Conversation(this.pan.getUsername(index), msgThdMngr);
            InetAddress ip_address = this.usersByUsername.get(this.pan.getUsername(index)).getIP();
            this.msgThdMngr.openConnection(ip_address,conv);
            System.out.println("IP address : " + ip_address);
            //this.msgThdMngr.openConnection(InetAddress.getLocalHost());

            //ChatPanel chatPanel = this.pan.addConversationTab(this.msgThdMngr.getConversationsAt(index).getName());
            ChatPanel chatPanel = this.pan.addConversationTab(this.msgThdMngr.getConversationByIP(ip_address).getName());
            this.tabByConv.put(ip_address,chatPanel);

            // ajout à la base de données
            // si la conversation est déjà présente dans la base de données on charge les messages
            // ouverture depuis moi
            Conversation conv2 = DatabaseManager.getConvByIp(ip_address, this.msgThdMngr);
            if (conv2 != null) {
                System.out.println("ip address opened from HERE : " + ip_address);
                List<Message> messages = DatabaseManager.getAllMessagesFromConv(conv2, true, this.msgThdMngr);
                System.out.println("conv already exists in database, " + messages.size() + " messages loaded");
                for (Message message :
                        messages) {
                    if (message.getIP().equals(ip_address)) {
                        this.addTextToTab(conv, message.getUser().getUsername() + ">" + message.getContent());
                    } else {
                        this.pan.addTextToTabAsSender(message.getContent());
                    }
                }
            } else {
                // condition peut etre retiree ?
                System.out.println("new conv in database");
                if (conv2 == null) {
                    DatabaseManager.addConversation(conv, ip_address);
                } else {
                    DatabaseManager.addConversation(conv2, ip_address);
                }
            }
        }
    }

    public void closeConversation(int index) {
        //this.msgThdMngr.close_conversation(index);
        InetAddress ip = this.usersByUsername.get(this.pan.getUsername(index)).getIP();
        this.msgThdMngr.close_conversation(ip);
    }

    public void sendMessage() {
        if (!this.pan.getTextfieldText().isBlank()) {
            this.pan.addTextToTabAsSender();
            //Message msg = new TextMessage(LocalDateTime.now(), this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
            Message msg = new TextMessage(LocalDateTime.now(), this.privateUser, this.msgThdMngr.getConversationsAt(this.pan.getSelectedIndex()), this.pan.getTextfieldText());
            System.out.println("TAB SELECTIONNE : "+ this.pan.getSelectedIndex());
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
    public void addConversationTab(Conversation conv) {
        // TODO faire ajouter les messages reçus depuis la bd
        ChatPanel chatPanel = this.pan.addConversationTab(conv.getConvName());
        //DatabaseManager.addConversation();
        InetAddress ip_address = null;
        if (conv.getUsersIP().size() > 0) {
            ip_address = conv.getUsersIP().get(0);
            this.tabByConv.put(ip_address,chatPanel);
            // si la conversation est déjà dans la base de données
            Conversation conv2 = DatabaseManager.getConvByIp(ip_address, this.msgThdMngr);
            if (conv2 != null) {
                List<Message> messages = DatabaseManager.getAllMessagesFromConv(conv2, true, this.msgThdMngr);
                System.out.println("conv already exists in database, " + messages.size() + " messages loaded");
                for (Message message :
                        messages) {
                    if (message.getIP().equals(ip_address)) {
                        System.out.println("message loaded from db");
                        this.addTextToTab(conv2, message.getUser().getUsername() + ">" + message.getContent());
                    } else {
                        this.pan.addTextToTabAsSender(message.getContent());
                    }
                }
            } else {
                System.out.println("new conv in database");
                DatabaseManager.addConversation(conv, ip_address);
            }
        }
    }

    public boolean changeUserName(String username) { //renvoie 0 si erreur
        boolean bool = this.privateUser.updateUsername(username);
        if (bool){
            this.userManager.sender(true);
        }
        return bool;
    }

    public void addContact(String username) {
        this.pan.addContact(username);
    }

    public void removeConversationTab(int index) {
        this.pan.removeConversationTab(index);
    }

    /*public void addTextToTab(int index, String text) {
        this.pan.addTextToTab(index, text);
    }*/

    public void addTextToTab(Conversation conv, String text) {
        tabByConv.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + " " + entry.getValue());
        });
        //this.pan.addTextToTab(tabByConv.get(conv), text);
        if (conv.getUsersIP().size() > 0) {
            this.pan.addTextToTab(this.tabByConv.get(conv.getUsersIP().get(0)), text);
        }
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
}
