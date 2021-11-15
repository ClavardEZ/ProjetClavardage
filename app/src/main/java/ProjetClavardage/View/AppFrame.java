package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;

public class AppFrame extends JFrame {

    public AppFrame() throws HeadlessException {
        super();
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        this.setVisible(true);
        this.add(new Pan());
    }
    
}
