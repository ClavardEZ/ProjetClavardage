package ProjetClavardage.View;

import ProjetClavardage.Model.Message;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Gère l'affichage des résultats de la popup de recherche de messages
 */
public class PopupSearchResult extends JFrame {
    public PopupSearchResult(List<Message> messages) {
        super("Résultat de la recherche");

        this.setVisible(true);
        
        DefaultListModel<String> results = new DefaultListModel<>();
        for (Message message :
                messages) {
            results.addElement(message.getContent());
        }

        JList list = new JList<>(results);
        JScrollPane scrollPane = new JScrollPane(list);

        this.add(scrollPane);
        this.pack();
    }
}
