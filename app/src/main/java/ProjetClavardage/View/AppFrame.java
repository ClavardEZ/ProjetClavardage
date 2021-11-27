package ProjetClavardage.View;

import org.checkerframework.checker.guieffect.qual.UI;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    public AppFrame(String title, int servPort, int clientPort, boolean maximised) throws HeadlessException {
        super();
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.add(new Pan(servPort, clientPort));
        this.pack();
        if (maximised) {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            this.setPreferredSize(new Dimension(900, 600));
        }


        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    
}
