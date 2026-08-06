import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class DatabaseView {
    private JFrame dbFrame;
    private JTable dbTable;
    private JComboBox<String> localeComboBox;
    private DefaultTableModel tableModel;
    private Database database;

    public DatabaseView(Database database) {
        this.database = database;
    }

    private void setFrame() {
        dbFrame = new JFrame();
        dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dbFrame.setSize(1200, 600);
    }

    public void createView() {
        SwingUtilities.invokeLater(() -> {
            setFrame();

            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            dbTable = new JTable(tableModel);
            dbFrame.add(new JScrollPane(dbTable), BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            //TODO dodać enkrypcję tłumaczeń
            localeComboBox = new JComboBox<>(new String[]{"pl_PL", "en_GB", "de_DE"});
            localeComboBox.addActionListener(e -> updateGuiLanguage());

            controlPanel.add(new JLabel("Language / Język:"));
            controlPanel.add(localeComboBox);
            dbFrame.add(controlPanel, BorderLayout.NORTH);

            updateGuiLanguage();
            dbFrame.setLocationRelativeTo(null);
            dbFrame.setVisible(true);
        });
    }

    private String[] setColumns(Locale targetLocale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", targetLocale);
        dbFrame.setTitle(resourceBundle.getString("table.title"));

        return new String[]{
                resourceBundle.getString("columns.country"),
                resourceBundle.getString("columns.departure"),
                resourceBundle.getString("columns.arrival"),
                resourceBundle.getString("columns.place"),
                resourceBundle.getString("columns.price"),
                resourceBundle.getString("columns.currency_symbol")
        };
    }

    private void updateGuiLanguage() {
        String selectedLocaleStr = (String) localeComboBox.getSelectedItem();
        assert selectedLocaleStr != null;
        String[] parts = selectedLocaleStr.split("_");
        Locale targetLocale = new Locale(parts[0], parts[1]);

        tableModel.setColumnIdentifiers(setColumns(targetLocale));
        tableModel.setRowCount(0);
        database.selectForGui(localeComboBox, tableModel);
    }

}
