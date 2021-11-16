package ProjetClavardage.Model;

import java.util.ArrayList;

/**
 * Classe qui gère les utilisateurs et leur connexions
 */
public class UserManager {
    private PrivateUser privateUser;
    private ArrayList<User> users;

    public UserManager(PrivateUser privateUser) {
        this.privateUser = privateUser;
        this.users = new ArrayList<>();
    }

    // modifie l'etat de connexion d'un utilisateur ? mettre en private ??
    public void setUserConnected(User user, boolean isConnected) {
        this.users.get(this.users.indexOf(user)).setConnected(isConnected);
    }

    // notifie aux autres utilisateurs que l'utilisateur courant est connecte
    // et envoie ses informations pour etre contacte
    public void selfConnected() {
    }

    // ecoute les connexion entrantes de nouveaux utilisateurs
    public void listenConnections() {}

    // ?
    public void notifyConnected() {
    }

    // ?
    public void isConnected(User user) {
    }

}
