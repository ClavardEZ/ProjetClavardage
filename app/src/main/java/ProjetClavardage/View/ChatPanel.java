package ProjetClavardage.View;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Classe gérant l'affichage de la fenêtre de chat
 */
public class ChatPanel extends JScrollPane {

    private Pan parent;
    private JLabel text;
    private DefaultListModel<String> messages;
    private JList<String> listeMessages;
    private ArrayList<Boolean> isSenderMessages;

    public static int NB_MSG_ECRAN = 30;

    public ChatPanel(Pan parent) {
        super();
        this.parent = parent;
        this.isSenderMessages = new ArrayList<>();

        this.messages = new DefaultListModel<>();
        this.listeMessages = new JList<>(this.messages);
        this.listeMessages.setLayoutOrientation(JList.VERTICAL);
        this.listeMessages.setVisibleRowCount(NB_MSG_ECRAN);
        this.listeMessages.setFont(new Font("Arial", Font.PLAIN, 10));
        this.listeMessages.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        this.setViewportView(this.listeMessages);

        this.listeMessages.setCellRenderer(new ChatPanelCellRenderer());

    }

    class ChatPanelCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (ChatPanel.this.isSenderMessages.get(index)) {
                c.setForeground(Color.BLUE);
            } else {
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }

    public void addText(String text, boolean isSender) {
        this.messages.addElement(text);
        this.isSenderMessages.add(isSender);
    }

    public void clearText() {
        this.messages.clear();
    }

}
