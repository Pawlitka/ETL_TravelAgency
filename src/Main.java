import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, SQLException {
        File dataDir = new File("data");
        TravelData travelData = new TravelData(dataDir);
        String dateFormat = "yyyy-MM-dd";
        for (String locale : Arrays.asList("pl_PL", "en_GB")) {
            List<String> odlist = travelData.getOffersDescriptionsList(locale, dateFormat);
            for (String od : odlist) System.out.println(od);
        }
        // --- część bazodanowa
        String url = "jdbc:h2:./travels_db"; /*<-- tu należy wpisać URL bazy danych */
        Database db = new Database(url, travelData);
        db.create();
        DatabaseView databaseView = new DatabaseView(db);
        databaseView.createView();
    }
}
