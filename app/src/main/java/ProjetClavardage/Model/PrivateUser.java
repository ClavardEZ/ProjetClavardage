package ProjetClavardage.Model;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Classe representant l'utilisateur courant du systeme
 */
public class PrivateUser extends User {
    private String password;

    public boolean updateUsername(String username) {
        if (DatabaseManager.getAllUserNames().contains(username)) {
            return false;
        }

        this.setUsername(username);
        DatabaseManager.changeUsername(this.getIP(), username);

        return true;
    }

    // not safe
    public void setPassword(String password) {
        this.password = password;
    }

    public PrivateUser(InetAddress IP, String username) {
        super(IP, -1, username);
    }


}
