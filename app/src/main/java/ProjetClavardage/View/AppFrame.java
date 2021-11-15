package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    public AppFrame() throws HeadlessException {
        super();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.add(new Pan());
        this.pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
}
