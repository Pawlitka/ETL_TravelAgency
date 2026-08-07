import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class TravelData {
    private final List<OfferEntity> offers = new ArrayList<>();

    TravelData(File fileDirectory) throws IOException {
        if (fileDirectory == null || !fileDirectory.isDirectory()) return;

        File[] files = fileDirectory.listFiles();
        if (files == null || files.length == 0) return;

        TsvFileReader tsvFileReader = new TsvFileReader(fileDirectory);
        for (File file : files) {
            if (file.isFile()) {
                List<TsvDTO> list = tsvFileReader.readFile(file);
                offers.addAll(TsvMapper.toEntity(list));
            }
        }
    }

    public List<String> getOffersDescriptionsList(String loc, String dateFormat) {
        TsvFileReader tsvFileReader = new TsvFileReader();
        List<String> offersToString = new ArrayList<>();
        Locale target = tsvFileReader.localize(loc);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, target);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(target);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("translations", target);

        for (OfferEntity offer : offers) {
            String country = translation(offer.country, offer.localization, target);
            String departure = simpleDateFormat.format(offer.departureDate);
            String arrival = simpleDateFormat.format(offer.arrivalDate);

            String place;

            if (offer.place != null && resourceBundle.containsKey(offer.place)) {
                place = resourceBundle.getString(offer.place);
            } else {
                place = offer.place;
            }

            String price = numberFormat.format(offer.price);
            String description = prepareDescription(country, departure, arrival, place, price, offer.currencySymbol);
            offersToString.add(description);
        }
        return offersToString;
    }

    public String translation(String country, Locale source, Locale target) {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.getDisplayCountry(source).equalsIgnoreCase(country)) {
                return loc.getDisplayCountry(target);
            }
        }
        return country;
    }

    public List<OfferEntity> getOffers() {
        return offers;
    }

    private String prepareDescription(String country, String departure, String arrival, String place, String price, String currencySymbol) {
        return String.format("%s %s %s %s %s %s", country, departure, arrival, place, price, currencySymbol);
    }
}
