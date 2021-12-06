package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextPlaceholderListener implements FocusListener {

    private JTextField textField;
    private String msg;
    private boolean isPlaceholder;

    public TextPlaceholderListener(JTextField textField, String msg) {
        this.textField = textField;
        this.msg = msg;
        this.isPlaceholder = true;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (textField.getText().equals(this.msg)) {
            textField.setForeground(Color.BLACK);
            textField.setText("");
            this.isPlaceholder = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (textField.getText().isEmpty()) {
            textField.setText(this.msg);
            textField.setForeground(Color.GRAY);
            this.isPlaceholder = true;
        }
    }

    public boolean isPlaceholder() {
        return isPlaceholder;
    }
}
