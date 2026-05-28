import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TravelData {
    private TsvMapper tsvMapper = new TsvMapper();
    private final List<OfferEntity> offers = new ArrayList<>();

    TravelData(File fileDirectory) throws IOException {
        TsvFileReader tsvFileReader = new TsvFileReader(fileDirectory);
        if (fileDirectory == null || !fileDirectory.isDirectory()) return;

        File[] files = fileDirectory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) {
                List<TsvDTO> list = tsvFileReader.readFile(file);
                offers.addAll(TsvMapper.toEntity(list));
            }
        }
    }

    public List<String> getOffersDescriptionsList(String loc, String dateFormat) {
        TsvFileReader tsvFileReader = new TsvFileReader();
        List<String> listOfOffers = new ArrayList<>();
        Locale target = tsvFileReader.localizationConvert(loc);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, target);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(target);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("offers", target);

        for (OfferEntity offer : offers) {
            String country = translation(offer.country, offer.localization, target);
            String departure = simpleDateFormat.format(offer.departureDate);
            String arrival = simpleDateFormat.format(offer.arrivalDate);

            String place = offer.place;

            try {
                place = resourceBundle.getString(offer.place);
            } catch (MissingResourceException _) {
            }

            String price = numberFormat.format(offer.price);

            String description = String.format("%s %s %s %s %s %s ", country, departure, arrival, place, price, offer.currencySymbol);
            listOfOffers.add(description);
        }
        return listOfOffers;
    }

    private String translation(String country, Locale source, Locale target) {
        for (Locale loc : Locale.getAvailableLocales()) {
            if (loc.getDisplayCountry(source).equalsIgnoreCase(country)) {
                return loc.getDisplayCountry(target);
            }
        }
        return country;
    }
}
