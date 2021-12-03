package ProjetClavardage.Model;

import java.net.InetAddress;

/**
 * Classe représentant un utilisateur externe du systeme
 */
public class User {
    private String username;
    private InetAddress IP;


    private boolean connected;

    public boolean isConnected() {
        // Automatically generated method. Please do not modify this code.
        return this.connected;
    }

    public void setConnected(boolean value) {
        // Automatically generated method. Please do not modify this code.
        this.connected = value;
    }

    public User(String login, String username) {
        this.username = username;
        this.connected = true;
    }

    public void setIP(InetAddress IP) {this.IP = IP;}
    public InetAddress getIP() {return this.IP;}
    public void setUsername(String username) {
        this.username = username;
    }

}
