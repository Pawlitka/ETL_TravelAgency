import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        if (offer.price == null) {
            offerStatement.setNull(6, Types.DOUBLE);
        } else {
            offerStatement.setDouble(6, offer.price);
        }
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
            throw new RuntimeException("Fails to create offers table: " + e);
        }
    }

    public List<OfferEntity> getAllOffers() {
        List<OfferEntity> offers = new ArrayList<>();

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

                    OfferEntity offer = new OfferEntity(
                            localization,
                            resultSet.getString("country"),
                            resultSet.getDate("departure_date"),
                            resultSet.getDate("arrival_date"),
                            resultSet.getString("place"),
                            resultSet.getDouble("price"),
                            resultSet.getString("currencySymbol")
                    );
                    offers.add(offer);
                }
            }
        } catch (Exception e) {
            System.out.println("Error message: " + e);
        }
        return offers;
    }
}
