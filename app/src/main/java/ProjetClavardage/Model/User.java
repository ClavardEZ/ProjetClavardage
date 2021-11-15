package ProjetClavardage.Model;

public class User {
    private String username;

    private String login;

    private boolean isConnected;

    private boolean isIsConnected() {
        // Automatically generated method. Please do not modify this code.
        return this.isConnected;
    }

    private void setIsConnected(boolean value) {
        // Automatically generated method. Please do not modify this code.
        this.isConnected = value;
    }

    public User() {
    }

    public User(String login) {
        this.login = login;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
