package Database;

import Entity.OfferEntity;
import OfferLayer.OfferStatement;
import TravelData.TravelData;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Database {
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final TravelData travelData;
    public Connection connection;

    public Database(TravelData travelData) {
        this.travelData = travelData;
    }

    public void create() throws SQLException {
        createEntity();
        createOfferBatch(connection);
    }

    private List<String> readPropertiesToConnect() {
        Properties properties = new Properties();
        List<String> values = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(".env")) {
            properties.load(fileInputStream);

            String user = properties.getProperty("USER");
            String password = properties.getProperty("PASSWORD");
            String url = properties.getProperty("URL");
            values.add(user);
            values.add(password);
            values.add(url);
        } catch (IOException e) {
            System.out.println("Cannot load file .env" + e);
        }
        return values;
    }

    public void connect() throws SQLException {
        List<String> values = readPropertiesToConnect();
        boolean isLengthValidOrValuesAreNull = values.size() < 3 || values.get(0) == null || values.get(1) == null || values.get(2) == null;
        if (isLengthValidOrValuesAreNull) {
            throw new IllegalStateException("Database connection properties are missing!");
        }
        boolean isConnectionNullOrClosed = connection == null || connection.isClosed();
        if (isConnectionNullOrClosed) {
            String user = values.get(0);
            String password = values.get(1);
            String url = values.get(2);
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
                + "price DECIMAL(10,2),"
                + "currency_code VARCHAR(3)"
                + ");";
    }

    private String prepareInsertOfferStatement() {
        return "INSERT INTO offers("
                + "locale, country_name, departure_date, arrival_date, place, price, currency_code)"
                + " VALUES(?,?,?,?,?,?,?);";
    }

    private void createOfferBatch(Connection connection) throws SQLException {

        try (OfferStatement offerStatement = new OfferStatement(connection.prepareStatement(prepareInsertOfferStatement()))) {
            connection.setAutoCommit(false); // Wyłączamy auto-commit
            for (OfferEntity offer : travelData.getOffers()) {
                offerStatement.setValues(offer);
                offerStatement.addBatch();
            }
            // Operacje wstawiania paczki...
            offerStatement.executeBatch();

            connection.commit(); // Zatwierdzamy dopiero, gdy wszystko poszło OK
        } catch (SQLException e) {
            connection.rollback(); // W razie błędu cofamy zmiany wstawiania
            throw new RuntimeException(e);
        } finally {
            connection.setAutoCommit(true); // Przywracamy domyślny stan
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
