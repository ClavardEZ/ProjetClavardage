package ProjetClavardage.Model;
import java.io.*;
import java.net.*;

public class TCPlistener extends Thread {
    int sock;
    public TCPlistener (int sock) {
        sock = sock;
    }

}
