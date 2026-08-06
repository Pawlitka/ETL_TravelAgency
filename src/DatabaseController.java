import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class DatabaseController {
    private final Database database;
    private final DatabaseView databaseView;
    private final TravelData travelData;

    public DatabaseController(Database database, DatabaseView databaseView, TravelData travelData) {
        this.database = database;
        this.databaseView = databaseView;
        this.travelData = travelData;

        this.databaseView.setLanguageChangeListener(e -> updateGuiForSelectedLanguage());
    }

    public void start() {
        databaseView.createView();
        updateGuiForSelectedLanguage();
    }

    public void updateGuiForSelectedLanguage() {
        String selectedLocaleStr = databaseView.getSelectedLanguage();
        if (selectedLocaleStr == null) {
            return;
        }

        String[] parts = selectedLocaleStr.split("_");
        Locale targetLocale = new Locale(parts[0], parts[1]);

        updateViewLabels(targetLocale);

        List<OfferEntity> offers = database.getAllOffers();
        Object[][] formattedData = formatOffersForView(offers, targetLocale);
        String[] columns = getColumnsTranslation(targetLocale);

        databaseView.updateTableContent(formattedData, columns);
    }

    private void updateViewLabels(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", locale);
        databaseView.setWindowTitle(resourceBundle.getString("table.title"));
    }

    private String[] getColumnsTranslation(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", locale);
        return new String[]{
                resourceBundle.getString("columns.country"),
                resourceBundle.getString("columns.departure"),
                resourceBundle.getString("columns.arrival"),
                resourceBundle.getString("columns.place"),
                resourceBundle.getString("columns.price"),
                resourceBundle.getString("columns.currency_symbol"),
        };
    }

    private Object[][] formatOffersForView(List<OfferEntity> offers, Locale targetLocale) {
        Object[][] rowData = new Object[offers.size()][6];

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", targetLocale);
        NumberFormat numberFormat = NumberFormat.getInstance(targetLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", targetLocale);

        for (int i = 0; i < offers.size(); i++) {
            OfferEntity offer = offers.get(i);

            String country = travelData.translation(offer.country, offer.localization, targetLocale);

            String departureData = simpleDateFormat.format(offer.departureDate);
            String arrivalDate = simpleDateFormat.format(offer.arrivalDate);

            String place = offer.place;
            if (resourceBundle.containsKey(offer.place)) {
                place = resourceBundle.getString(offer.place);
            }

            String price = numberFormat.format(offer.price);
            String currencySymbol = offer.currencySymbol;
            rowData[i] = new Object[]{
                    country, departureData, arrivalDate, place, price, currencySymbol
            };
        }
        return rowData;
    }
}
