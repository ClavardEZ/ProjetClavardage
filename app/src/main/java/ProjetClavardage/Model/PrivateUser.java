package ProjetClavardage.Model;

import java.net.InetAddress;

/**
 * Classe representant l'utilisateur courant du systeme
 */
public class PrivateUser extends User {
    private String password;

    public void updateUsername(String username) {
        // broadcast sur le reseau pour voir si le pseudo est dispo
        // si dispo alors maj
    }

    // not safe
    public void setPassword(String password) {
        this.password = password;
    }

    public PrivateUser(InetAddress IP,int port, String username) {
        super(IP, port, username);
    }


}
