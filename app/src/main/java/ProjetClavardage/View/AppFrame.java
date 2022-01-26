package ProjetClavardage.View;

import ProjetClavardage.Controller.MainController;
import org.checkerframework.checker.guieffect.qual.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AppFrame extends JFrame {

    ;
    public AppFrame(String title, int servPort, int clientPort, int listeningPort, int sendingPort, boolean maximised, String ni) throws HeadlessException {
        super();
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        MainController mc = new MainController(servPort, clientPort, listeningPort, sendingPort, title, ni);
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
                System.out.println("LALALALALA Closed");
                e.getWindow().dispose();
            }
        });
    }


    
}
