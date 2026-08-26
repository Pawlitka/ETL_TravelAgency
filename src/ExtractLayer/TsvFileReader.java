package ExtractLayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TsvFileReader {
    private static final Integer NUMBER_OF_VALUES_PER_LINE = 7;
    private static final String TABULATOR = "\t";
    private final List<TsvDTO> records = new ArrayList<>();
    private File fileDirectory;

    public TsvFileReader(File fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public List<TsvDTO> readFile(File fileDirectory) throws IOException {
        records.clear();
        BufferedReader br = new BufferedReader(new FileReader(fileDirectory));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(TABULATOR);
                if (data.length == NUMBER_OF_VALUES_PER_LINE) {
                    Locale localization = localize(data[0]);

                    String country = data[1];


                    Date departureDate = parseData(data[2]);
                    Date arrivalDate = parseData(data[3]);

                    String place = normalizePlaces(data[4], localization);
                    Double price = parsePrice(localization, data[5]);
                    String currencySymbol = data[6];

                    records.add(
                            new TsvDTO(
                                    localization, country, departureDate, arrivalDate, place, price, currencySymbol));
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error message: " + e);
        } finally {
            br.close();
        }
        return records;
    }

    public Locale localize(String text) {
        String[] localizationParts = text.split("_");
        if (localizationParts.length != 2) {
            throw new IllegalArgumentException("Invalid locale format: " + text);
        }
        return new Locale(localizationParts[0], localizationParts[1]);
    }

    public Date parseData(String text) {
        LocalDate localDate = LocalDate.parse(text);

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Double parsePrice(Locale localization, String text) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(localization);
        Number number = format.parse(text);
        return number.doubleValue();
    }

    private String normalizePlaces(String place, Locale localization) {
        boolean isPlaceNullOrBlanc = place == null || place.isBlank();
        if (isPlaceNullOrBlanc) return "";

        String keyProperty = place.toLowerCase().trim();

        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("places", localization);

            if (resourceBundle.containsKey(keyProperty)) {
                return resourceBundle.getString(keyProperty);
            }
        } catch (MissingResourceException e) {
            System.out.println("Error message: " + e);
        }
        return place;
    }
}