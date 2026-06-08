import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Database {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
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

    public void create() throws SQLException {
        createEntity();
        createOfferBatch(connection);
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

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String user = "sa";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
        }
    }

    private void updateGuiLanguage() {
        String selectedLocaleStr = (String) localeComboBox.getSelectedItem();
        String[] parts = selectedLocaleStr.split("_");
        Locale targetLocale = new Locale(parts[0], parts[1]);

        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", targetLocale);

        dbFrame.setTitle(resourceBundle.getString("table.title"));
        String[] columnHeaders = {
                resourceBundle.getString("columns.country"),
                resourceBundle.getString("columns.departure"),
                resourceBundle.getString("columns.arrival"),
                resourceBundle.getString("columns.place"),
                resourceBundle.getString("columns.price"),
                resourceBundle.getString("columns.currency_symbol")
        };
        tableModel.setColumnIdentifiers(columnHeaders);
        tableModel.setRowCount(0);
        selectForGui();
    }

    private String prepareRemoveOfferTableStatement() {
        return "DROP TABLE IF EXISTS offers;";
    }

    private String prepareCreateOfferTableStatement() {
        return "CREATE TABLE offers ("
                + "id INT AUTO_INCREMENT PRIMARY KEY,"
                + "locale VARCHAR(20),"
                + "country VARCHAR(200),"
                + "departure_date VARCHAR(30),"
                + "arrival_date VARCHAR(30),"
                + "place VARCHAR(40),"
                + "price DOUBLE,"
                + "currencySymbol VARCHAR(30)"
                + ");";
    }

    private String prepareInsertOfferStatement() {
        return "INSERT INTO offers("
                + "locale, country, departure_date, arrival_date, place, price, currencySymbol)"
                + " VALUES(?,?,?,?,?,?,?);";
    }

    private void createOfferBatch(
            Connection connection) throws SQLException {

        try (PreparedStatement offerStatement = connection.prepareStatement(prepareInsertOfferStatement())) {
            for (OfferEntity offer : travelData.getOffers()) {
                setValues(offerStatement, offer);
                offerStatement.addBatch();
            }
            offerStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement setValues(PreparedStatement offerStatement, OfferEntity offer) throws SQLException {
        offerStatement.setString(1, offer.localization.toString());
        offerStatement.setString(2, offer.country);
        offerStatement.setString(3, DATE_FORMAT.format(offer.departureDate));
        offerStatement.setString(4, DATE_FORMAT.format(offer.arrivalDate));
        offerStatement.setString(5, offer.place);
        offerStatement.setString(6, String.valueOf(offer.price));
        offerStatement.setString(7, offer.currencySymbol);
        return offerStatement;
    }

    private void createEntity() {
        try {
            connect();
            try (Statement statement = connection.createStatement()) {
                statement.execute(prepareRemoveOfferTableStatement());
                statement.execute(prepareCreateOfferTableStatement());
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
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", targetLocale);

        String selectSQL = "SELECT * FROM offers";
        try {
            connect();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSQL)
            ) {
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
