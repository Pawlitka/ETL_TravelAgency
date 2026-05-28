import java.sql.*;
import java.text.SimpleDateFormat;

public class Database {
    private final String url;
    private TravelData travelData;
    private Connection connection;

    public Database(String url, TravelData travelData) {
        this.url = url;
        this.travelData = travelData;
    }

    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url);
        }
    }

    public void create() throws SQLException {
        String droppingTable = "DROP TABLE IF EXISTS offers;";
        String creatingTable = "CREATE TABLE offers(id INT AUTO_INCREMENT PRIMARY KEY," +
                "locale Varchar(10)," +
                "country Varchar(50)," +
                "departure_date DATE," +
                "arrival_date DATE," +
                "place VARCHAR(50)," +
                "price DOUBLE," +
                "currencySymbol VARCHAR(20)" +
                ");";
        String insert = "INSERT INTO offers(locale, country, departure_date, arrival_date, place, price, currencySymbol)" +
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
                }
                preparedStatement.executeBatch();
            }
        } catch (SQLException _) {

        }
    }
}
