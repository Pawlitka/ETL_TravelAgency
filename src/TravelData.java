import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
}
