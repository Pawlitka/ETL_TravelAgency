import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class Database {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final String url;
    private final TravelData travelData;
    private Connection connection;

    public Database(String url, TravelData travelData) {
        this.url = url;
        this.travelData = travelData;
    }

    public void create() throws SQLException {
        createEntity();
        createOfferBatch(connection);
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String user = "sa";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
        }
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

    private void setValues(PreparedStatement offerStatement, OfferEntity offer) throws SQLException {
        offerStatement.setString(1, offer.localization.toString());
        offerStatement.setString(2, offer.country);
        offerStatement.setString(3, DATE_FORMAT.format(offer.departureDate));
        offerStatement.setString(4, DATE_FORMAT.format(offer.arrivalDate));
        offerStatement.setString(5, offer.place);
        offerStatement.setString(6, String.valueOf(offer.price));
        offerStatement.setString(7, offer.currencySymbol);
    }

    private void createEntity() {
        try {
            connect();
            try (Statement statement = connection.createStatement()) {
                statement.execute(prepareRemoveOfferTableStatement());
                statement.execute(prepareCreateOfferTableStatement());
            }
        } catch (SQLException e) {
            System.out.println("Error message: " + e);
        }
    }

    public void selectForGui(JComboBox<String> localeComboBox, DefaultTableModel tableModel) {
        String selectedLocaleStr = (String) localeComboBox.getSelectedItem();
        assert selectedLocaleStr != null;
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
            System.out.println("Error message: " + e);
        }
    }
}
