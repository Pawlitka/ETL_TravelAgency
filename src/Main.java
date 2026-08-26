import DatabaseLayer.Database;
import DatabaseLayer.DatabaseController;
import DatabaseLayer.DatabaseView;
import OfferLayer.OfferRepository;
import TravelData.TravelData;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        File dataDir = new File("data");
        TravelData travelData = new TravelData(dataDir);
        String url = "jdbc:h2:./travels_db";

        Database database = new Database(url, travelData);
        DatabaseView databaseView = new DatabaseView();
        OfferRepository offerRepository = new OfferRepository(database);

        database.create();
        DatabaseController databaseController = new DatabaseController(offerRepository, databaseView, travelData);
        databaseController.start();
    }
}
