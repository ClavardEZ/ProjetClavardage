package ProjetClavardage.View;

import ProjetClavardage.Controller.MainController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Fenêtre vide
 */
public class AppFrame extends JFrame {

    ;
    public AppFrame(String title, int servPort, int clientPort, int listeningPort, int sendingPort, boolean maximised) throws HeadlessException {
        super();
        ImageIcon img = new ImageIcon(this.getClass().getResource("/logo_white.png"));
        this.setIconImage(img.getImage());
        this.setTitle("ClavardEZ");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        MainController mc = new MainController(servPort, clientPort, listeningPort, sendingPort, title);
        this.add(mc.getPan());
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
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                mc.closingApp();
                e.getWindow().dispose();
            }
        });
    }


    
}
