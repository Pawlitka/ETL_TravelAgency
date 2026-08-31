package Database;

import OfferLayer.OfferModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class OfferScreenView {
    private final JComboBox<String> localeComboBox;
    private final DefaultTableModel tableModel;
    private final String[] LOCALE_LANGUAGES = new String[]{"pl_PL", "en_GB", "de_DE"};
    private final String LABEL_LANGUAGES = "Language / Język:";
    private final Integer WIDTH = 1200;
    private final Integer HEIGHT = 600;
    private final Component SCREEN_CENTER = null;
    private JFrame dbFrame;


    public OfferScreenView() {
        setFrame();

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable dbTable = new JTable(tableModel);
        dbFrame.add(new JScrollPane(dbTable), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        localeComboBox = new JComboBox<>(LOCALE_LANGUAGES);

        controlPanel.add(new JLabel(LABEL_LANGUAGES));
        controlPanel.add(localeComboBox);
        dbFrame.add(controlPanel, BorderLayout.NORTH);

        dbFrame.setLocationRelativeTo(SCREEN_CENTER);
    }

    private void setFrame() {
        dbFrame = new JFrame();
        dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbFrame.setSize(WIDTH, HEIGHT);
    }

    public void createView() {
        SwingUtilities.invokeLater(() -> dbFrame.setVisible(true));
    }

    public void setLanguageChangeListener(ActionListener listener) {
        localeComboBox.addActionListener(listener);
    }

    public String getSelectedLanguage() {
        return (String) localeComboBox.getSelectedItem();
    }

    public void updateViewLabels(OfferModel data, String[] column) {
        tableModel.setColumnIdentifiers(column);
        tableModel.setRowCount(0);
        for (int i = 0; i < data.getRowCount(); i++) {
            tableModel.addRow(data.getRow(i));
        }
    }

    public void setWindowTitle(String title) {
        dbFrame.setTitle(title);
    }
}