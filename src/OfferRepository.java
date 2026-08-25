import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


public class OfferRepository {
    private Connection connection;

    public OfferRepository() {
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

    private void connect() throws SQLException {
        List<String> values = readPropertiesToConnect();
        if (connection == null || connection.isClosed()) {
            String user = values.get(0);
            String password = values.get(1);
            String url = values.get(2);
            connection = DriverManager.getConnection(url, user, password);
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
                            resultSet.getString("country_name"),
                            resultSet.getDate("departure_date"),
                            resultSet.getDate("arrival_date"),
                            resultSet.getString("place"),
                            resultSet.getDouble("price"),
                            resultSet.getString("currency_code")
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
