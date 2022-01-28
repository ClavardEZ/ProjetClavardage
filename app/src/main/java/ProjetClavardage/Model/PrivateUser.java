package ProjetClavardage.Model;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Classe representant l'utilisateur courant du systeme
 */
public class PrivateUser extends User {
    private String password;

    public PrivateUser(InetAddress IP, String username) {
        super(IP, -1, username);
    }

    /**
     *  Met à jour le nom d'utilisateur courant dans la base de donnée si possible
     * @param username Nouvel username
     * @return  renvoie false si le pseudo est déjà utilisé par un autre utilisateur connu, vrai sinon
     */
    public boolean updateUsername(String username) {
        if (DatabaseManager.getAllUserNames().contains(username)) {
            return false;
        }

        this.setUsername(username);
        DatabaseManager.changeUsername(this.getIP(), username);

        return true;
    }

    @Deprecated
    public void setPassword(String password) {
        this.password = password;
    }


}
