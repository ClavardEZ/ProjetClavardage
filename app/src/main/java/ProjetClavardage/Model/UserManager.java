package ProjetClavardage.Model;
import java.io.IOException;
import java.util.ArrayList;
import java.net.*;
import java.util.Enumeration;

/**
 * Classe qui gère les utilisateurs et leur connexions
 */
public class UserManager extends Thread {
    private PrivateUser privateUser;
    private int NUM_PORT=9002;
    DatagramSocket dgramSocket;

    //On n'instancie que les utilisateurs connectes, les autres sont trouvables dans la BDD
    private ArrayList<User> connected_users;

    public UserManager(PrivateUser privateUser) {
        this.privateUser = privateUser;
        this.connected_users = new ArrayList<>();
    }

    // modifie l'etat de connexion d'un utilisateur ? mettre en private ??
    public void setUserConnected(User user) {
        if (!this.connected_users.contains(user))
        {
            DatabaseManager.addUser(user.getIP().toString(),user.getUsername());
            this.connected_users.add(user);
        }
    }

    public void disconnectUser(User user){
        this.connected_users.remove(user);
    }

    // notifie aux autres utilisateurs que l'utilisateur courant est connecte
    // et envoie ses informations pour etre contacte
    public void selfConnected() {
    }

    public void sender(boolean b) { // b==true correspond a une connexion/changment de pseudo, b==false correspond a une deconnexion
        String message = "XX";
        if (b) {
            message = this.privateUser.getUsername();
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements())
            {
                NetworkInterface networkInterface = interfaces.nextElement();
                    if (networkInterface.isLoopback())
                        continue;    // Do not want to use the loopback interface.
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses())
                {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null)
                        continue;

                    DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                            message.length(),broadcast, NUM_PORT);
                    dgramSocket.send(outPacket);
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*try {
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                    message.length(),InetAddress.getLocalHost(), NUM_PORT);
            dgramSocket.send(outPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*DatagramPacket outPacket = new DatagramPacket(message.getBytes(),
                message.length(),host, port);*/
    }

    public void start_listener() {
        try {
            this.dgramSocket = new DatagramSocket(NUM_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public void close_listener() {
        this.sender(false);
        this.dgramSocket.close();
    }

    // ecoute les connexion entrantes de nouveaux utilisateurs
    public void run() {
        try {
            byte[] buffer = new byte[256];
            InetAddress lastAddress = null;
            while(true) {
                DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
                System.out.println("UDP :"+"waiting..." );
                dgramSocket.receive(inPacket);
                InetAddress clientAddress = inPacket.getAddress();
                if (clientAddress == lastAddress) {
                    continue;
                }
                lastAddress = clientAddress;
                int clientPort = inPacket.getPort();
                String message = new String(inPacket.getData(), 0, inPacket.getLength());
                System.out.println("UDP :"+message );
                if (message.length()<3) { //un message de moins de 3 caractères correspond à une deconnexion
                    User user = new User(clientAddress,clientPort,message);
                    setUserConnected(user);  //remplissage du tableau connected_users
                    if (message!=user.getUsername()) {user.setUsername(message);}
                }
                else{
                    connected_users.remove(new User(clientAddress,clientPort,message));
                }
                String response= privateUser.getUsername();
                DatagramPacket outPacket = new DatagramPacket(response.getBytes(), response.length(),
                                                                clientAddress, clientPort);
                dgramSocket.send(outPacket);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ?
    public void notifyConnected() {
    }

    // ?
    public boolean isConnected(User user) {
        return this.connected_users.contains(user);
    }

}
