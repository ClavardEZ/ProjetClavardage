package ProjetClavardage.Model;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * Classe représentant un utilisateur externe du systeme
 */
public class User implements Serializable {
    private String username; //taille >2 char
    private InetAddress IP;
    private int port;


    private boolean connected;

    public boolean isConnected() {
        // Automatically generated method. Please do not modify this code.
        return this.connected;
    }

    public void setConnected(boolean value) {
        // Automatically generated method. Please do not modify this code.
        this.connected = value;
    }

    public User(InetAddress IP, int port, String username) {
        this.username = username;
        this.IP=IP;
        this.port=port;
        this.connected = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equal(IP, user.IP);
    }

    public void setIP(InetAddress IP) {this.IP = IP;}
    public InetAddress getIP() {return this.IP;}
    public String getUsername() {return this.username;}
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", IP=" + IP +
                '}';
    }
}
