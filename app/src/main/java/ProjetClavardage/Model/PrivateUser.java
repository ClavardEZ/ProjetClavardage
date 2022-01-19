package ProjetClavardage.Model;

import java.net.InetAddress;

/**
 * Classe representant l'utilisateur courant du systeme
 */
public class PrivateUser extends User {
    private String password;

    public boolean updateUsername(String username) {
        // TODO checker si le nouveau pseudo est déjà dans la base de donnée sinon, on l'ajoute
        this.setUsername(username);

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
