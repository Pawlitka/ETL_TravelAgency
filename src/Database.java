import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Database {
    private final String url;


    private TravelData travelData;
    private Connection connection;
    private JFrame frame;
    private JTable table;
    private JComboBox<String> localeComboBox;
    private DefaultTableModel tableModel;

    public Database(String url, TravelData travelData) {
        this.url = url;
        this.travelData = travelData;
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, "sa", "");
        }
    }

    public void create() throws SQLException {
        String droppingTable = "DROP TABLE IF EXISTS offers;";

        String creatingTable = "CREATE TABLE offers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "locale VARCHAR(10)," +
                "country VARCHAR(100)," +
                "departure_date VARCHAR(10)," +
                "arrival_date VARCHAR(10)," +
                "place VARCHAR(50)," +
                "price DOUBLE," +
                "currencySymbol VARCHAR(10)" +
                ");";

        String insert = "INSERT INTO offers(" +
                "locale, country, departure_date, arrival_date, place, price, currencySymbol)" +
                " VALUES(?,?,?,?,?,?,?);";

        try {
            connect();
            try (Statement statement = connection.createStatement()) {
                statement.execute(droppingTable);
                statement.execute(creatingTable);
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try (PreparedStatement preparedStatement = connection.prepareStatement(insert)) {
                for (OfferEntity offer : travelData.getOffers()) {
                    preparedStatement.setString(1, offer.localization.toString());
                    preparedStatement.setString(2, offer.country);
                    preparedStatement.setString(3, simpleDateFormat.format(offer.departureDate));
                    preparedStatement.setString(4, simpleDateFormat.format(offer.arrivalDate));
                    preparedStatement.setString(5, offer.place);
                    preparedStatement.setString(6, String.valueOf(offer.price));
                    preparedStatement.setString(7, offer.currencySymbol);

                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showGui() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 400);

            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            table = new JTable(tableModel);
            frame.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            localeComboBox = new JComboBox<>(new String[]{"pl_PL", "en_GB"});
            localeComboBox.addActionListener(e -> updateGuiLanguage());

            controlPanel.add(new JLabel("Language / Język:"));
            controlPanel.add(localeComboBox);
            frame.add(controlPanel, BorderLayout.NORTH);

            updateGuiLanguage();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void updateGuiLanguage() {
        String selectedLocaleStr = (String) localeComboBox.getSelectedItem();
        String[] parts = selectedLocaleStr.split("_");
        Locale targetLocale = new Locale(parts[0], parts[1]);

        ResourceBundle rb = ResourceBundle.getBundle("offers", targetLocale);
        SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd", targetLocale);
        NumberFormat nf = NumberFormat.getInstance(targetLocale);

        // Aktualizacja tytułu okna i nagłówków kolumn
        frame.setTitle(rb.getString("title"));
        String[] columnHeaders = {
                rb.getString("column_country"),
                rb.getString("column_departure"),
                rb.getString("column_arrival"),
                rb.getString("column_place"),
                rb.getString("column_price"),
                rb.getString("column_currencySymbol")
        };
        tableModel.setColumnIdentifiers(columnHeaders);
        tableModel.setRowCount(0);

        String selectSQL = "SELECT * FROM offers";
        try {
            connect();
            try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(selectSQL)) {
                while (rs.next()) {

                    String srcLocStr = rs.getString("locale");
                    String[] sParts = srcLocStr.split("_");
                    Locale srcLocale = new Locale(sParts[0], sParts[1]);

                    String rawCountry = rs.getString("country");
                    String country = travelData.translation(rawCountry, srcLocale, targetLocale);

                    String depStr = outputSdf.format(rs.getDate("departure_date"));
                    String retStr = outputSdf.format(rs.getDate("arrival_date"));

                    String destKey = rs.getString("place");
                    String dest = destKey;
                    try {
                        dest = rb.getString(destKey);
                    } catch (Exception _) {
                    }

                    String priceStr = nf.format(rs.getDouble("price"));
                    String currency = rs.getString("currencySymbol");

                    tableModel.addRow(new Object[]{country, depStr, retStr, dest, priceStr, currency});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
