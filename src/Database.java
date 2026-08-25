import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class Database {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final String url;
    private final TravelData travelData;
    private Connection connection;

    public Database(String url, TravelData travelData) {
        this.url = url;
        this.travelData = travelData;
    }

    public void create() {
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
                + "locale VARCHAR(5),"
                + "country_name VARCHAR(200),"
                + "departure_date DATE,"
                + "arrival_date DATE,"
                + "place VARCHAR(40),"
                + "price DOUBLE,"
                + "currency_code VARCHAR(3)"
                + ");";
    }

    private String prepareInsertOfferStatement() {
        return "INSERT INTO offers("
                + "locale, country_name, departure_date, arrival_date, place, price, currency_code)"
                + " VALUES(?,?,?,?,?,?,?);";
    }

    private void createOfferBatch(Connection connection) {

        try (OfferStatement offerStatement = new OfferStatement(connection.prepareStatement(prepareInsertOfferStatement()))) {
            for (OfferEntity offer : travelData.getOffers()) {
                offerStatement.setValues(offer);
                offerStatement.addBatch();
            }
            offerStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
}
