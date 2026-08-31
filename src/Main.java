import Database.Database;
import Database.OfferScreenController;
import Database.OfferScreenView;
import Repository.OfferRepository;
import TravelData.TravelData;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        File dataDir = new File("data");
        TravelData travelData = new TravelData(dataDir);

        Database database = new Database(travelData);
        OfferScreenView databaseView = new OfferScreenView();
        OfferRepository offerRepository = new OfferRepository(database);

        database.create();
        OfferScreenController databaseController = new OfferScreenController(offerRepository, databaseView, travelData);
        databaseController.start();
    }
}
