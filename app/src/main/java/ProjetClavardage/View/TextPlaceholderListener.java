package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class TextPlaceholderListener implements FocusListener {

    private JTextField textField;
    private String msg;

    public TextPlaceholderListener(JTextField textField, String msg) {
        this.textField = textField;
        this.msg = msg;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (textField.getText().equals(this.msg)) {
            textField.setForeground(Color.BLACK);
            textField.setText("");
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (textField.getText().isEmpty()) {
            textField.setText(this.msg);
            textField.setForeground(Color.GRAY);
        }
    }

}
