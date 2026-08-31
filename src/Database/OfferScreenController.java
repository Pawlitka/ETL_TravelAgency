package Database;

import Entity.OfferEntity;
import OfferLayer.OfferModel;
import Repository.OfferRepository;
import TravelData.TravelData;
import UtilityClass.LocaleFactory;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class OfferScreenController {
    private static final Integer NUMBER_OF_COLUMNS = 6;
    private final OfferScreenView databaseView;
    private final TravelData travelData;
    private final OfferRepository offerRepository;
    private List<OfferEntity> cachedOffers = new ArrayList<>();
    private Locale currentLocale;
    private ResourceBundle currentBundle;

    public OfferScreenController(OfferRepository offerRepository, OfferScreenView databaseView, TravelData travelData) {
        this.offerRepository = offerRepository;
        this.databaseView = databaseView;
        this.travelData = travelData;

        this.databaseView.setLanguageChangeListener(_ -> updateGuiForSelectedLanguage());
    }

    public void start() {
        databaseView.createView();
        cachedOffers = offerRepository.getAllOffers();

        updateGuiForSelectedLanguage();
    }

    private void updateViewLabels() {
        databaseView.setWindowTitle(currentBundle.getString("table.title"));
    }

    private void updateTableContent() {
        String[] columns = getColumnsTranslation(currentLocale);

        OfferModel formattedData = formatOffersForView(cachedOffers, currentLocale);

        databaseView.updateViewLabels(formattedData, columns);
    }

    public void updateGuiForSelectedLanguage() {
        String selectedLocaleStr = databaseView.getSelectedLanguage();
        if (selectedLocaleStr == null) {
            return;
        }

        Locale newLocale = LocaleFactory.fromString(selectedLocaleStr);

        if (!newLocale.equals(currentLocale)) {
            currentLocale = newLocale;
            currentBundle = ResourceBundle.getBundle("translations", currentLocale);
        }

        updateViewLabels();
        updateTableContent();
    }

    private String[] getColumnsTranslation(Locale locale) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", locale);
        return new String[]{
                resourceBundle.getString("columns.country"),
                resourceBundle.getString("columns.departure"),
                resourceBundle.getString("columns.arrival"),
                resourceBundle.getString("columns.place"),
                resourceBundle.getString("columns.price"),
                resourceBundle.getString("columns.currency_code"),
        };
    }

    private OfferModel formatOffersForView(List<OfferEntity> offers, Locale targetLocale) {
        Object[][] rowData = new Object[offers.size()][NUMBER_OF_COLUMNS];

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", targetLocale);
        NumberFormat numberFormat = NumberFormat.getInstance(targetLocale);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", targetLocale);

        for (int i = 0; i < offers.size(); i++) {
            OfferEntity offer = offers.get(i);

            String country = travelData.translation(offer.countryName(), offer.localization(), targetLocale);

            String departureData = simpleDateFormat.format(offer.departureDate());
            String arrivalDate = simpleDateFormat.format(offer.arrivalDate());

            String place = offer.place();
            if (resourceBundle.containsKey(offer.place())) {
                place = resourceBundle.getString(offer.place());
            }

            String price = numberFormat.format(offer.price());
            String currencySymbol = offer.currencyCode();
            rowData[i] = new Object[]{
                    country, departureData, arrivalDate, place, price, currencySymbol
            };
        }
        return new OfferModel(rowData);
    }
}
