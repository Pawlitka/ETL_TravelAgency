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
    private JFrame dbFrame;
    private JTable dbTable;
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
        setConnection();
        createInsertStatement();
    }

    public void showGui() {
        SwingUtilities.invokeLater(() -> {
            dbFrame = new JFrame();
            dbFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            dbFrame.setSize(1200, 600);

            tableModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            dbTable = new JTable(tableModel);
            dbFrame.add(new JScrollPane(dbTable), BorderLayout.CENTER);

            JPanel controlPanel = new JPanel();
            localeComboBox = new JComboBox<>(new String[]{"pl_PL", "en_GB"});
            localeComboBox.addActionListener(e -> updateGuiLanguage());

            controlPanel.add(new JLabel("Language / Język:"));
            controlPanel.add(localeComboBox);
            dbFrame.add(controlPanel, BorderLayout.NORTH);

            updateGuiLanguage();
            dbFrame.setLocationRelativeTo(null);
            dbFrame.setVisible(true);
        });
    }

    private void updateGuiLanguage() {
        String selectedLocaleStr = (String) localeComboBox.getSelectedItem();
        String[] parts = selectedLocaleStr.split("_");
        Locale targetLocale = new Locale(parts[0], parts[1]);

        ResourceBundle resourceBundle = ResourceBundle.getBundle("offers", targetLocale);

        dbFrame.setTitle(resourceBundle.getString("title"));
        String[] columnHeaders = {
                resourceBundle.getString("column_country"),
                resourceBundle.getString("column_departure"),
                resourceBundle.getString("column_arrival"),
                resourceBundle.getString("column_place"),
                resourceBundle.getString("column_price"),
                resourceBundle.getString("column_currencySymbol")
        };
        tableModel.setColumnIdentifiers(columnHeaders);
        tableModel.setRowCount(0);
        selectForGui();
    }

    private String removeTable() {
        return "DROP TABLE IF EXISTS offers;";
    }

    private String createTable() {
        return "CREATE TABLE offers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "locale VARCHAR(20)," +
                "country VARCHAR(200)," +
                "departure_date VARCHAR(30)," +
                "arrival_date VARCHAR(30)," +
                "place VARCHAR(40)," +
                "price DOUBLE," +
                "currencySymbol VARCHAR(30)" +
                ");";
    }

    private String insert() {
        return "INSERT INTO offers(" +
                "locale, country, departure_date, arrival_date, place, price, currencySymbol)" +
                " VALUES(?,?,?,?,?,?,?);";
    }

    private void createInsertStatement() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try (PreparedStatement preparedStatement = connection.prepareStatement(insert())) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setConnection() {
        try {
            connect();
            try (Statement statement = connection.createStatement()) {
                statement.execute(removeTable());
                statement.execute(createTable());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void selectForGui() {
        String selectedLocaleStr = (String) localeComboBox.getSelectedItem();
        String[] parts = selectedLocaleStr.split("_");
        Locale targetLocale = new Locale(parts[0], parts[1]);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", targetLocale);
        NumberFormat numberFormat = NumberFormat.getInstance(targetLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("offers", targetLocale);
        
        String selectSQL = "SELECT * FROM offers";
        try {
            connect();
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(selectSQL)) {
                while (resultSet.next()) {

                    String localeLabel = resultSet.getString("locale");
                    String[] sParts = localeLabel.split("_");
                    Locale localization = new Locale(sParts[0], sParts[1]);

                    String countryLabel = resultSet.getString("country");
                    String country = travelData.translation(countryLabel, localization, targetLocale);

                    String departureDate = simpleDateFormat.format(resultSet.getDate("departure_date"));
                    String arrivalDate = simpleDateFormat.format(resultSet.getDate("arrival_date"));

                    String placeLabel = resultSet.getString("place");
                    String place = placeLabel;
                    try {
                        place = resourceBundle.getString(placeLabel);
                    } catch (Exception _) {
                    }

                    String price = numberFormat.format(resultSet.getDouble("price"));
                    String currencySymbol = resultSet.getString("currencySymbol");

                    tableModel.addRow(new Object[]{country, departureDate, arrivalDate, place, price, currencySymbol});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
