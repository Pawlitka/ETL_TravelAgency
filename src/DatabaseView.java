import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class DatabaseView {
    private final JComboBox<String> localeComboBox;
    private final DefaultTableModel tableModel;
    private JFrame dbFrame;

    public DatabaseView() {
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
        localeComboBox = new JComboBox<>(new String[]{"pl_PL", "en_GB", "de_DE"});

        controlPanel.add(new JLabel("Language / Język:"));
        controlPanel.add(localeComboBox);
        dbFrame.add(controlPanel, BorderLayout.NORTH);

        dbFrame.setLocationRelativeTo(null);
    }

    private void setFrame() {
        dbFrame = new JFrame();
        dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbFrame.setSize(1200, 600);
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

    public void updateTableContent(Object[][] data, String[] column) {
        tableModel.setColumnIdentifiers(column);
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    public void setWindowTitle(String title) {
        dbFrame.setTitle(title);
    }
}