package TSV;

import UtilityClass.LocaleFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class TsvFileReader {
    private static final Integer NUMBER_OF_VALUES_PER_LINE = 7;
    private static final String TABULATOR = "\t";
    private final List<TsvOfferDTO> records = new ArrayList<>();
    private File fileDirectory;


    public TsvFileReader(File fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public List<TsvOfferDTO> readFile(File fileDirectory) throws IOException {
        records.clear();
        try (BufferedReader br = java.nio.file.Files.newBufferedReader(
                fileDirectory.toPath(), java.nio.charset.StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;

                String[] data = line.split(TABULATOR, -1);
                if (data.length != NUMBER_OF_VALUES_PER_LINE) {
                    throw new IllegalArgumentException("Error in file (line: " + lineNumber + ")");
                }

                Locale localization = localize(data[0]);

                String country = data[1];


                Date departureDate = parseData(data[2]);
                Date arrivalDate = parseData(data[3]);

                String place = normalizePlaces(data[4], localization);
                BigDecimal price = BigDecimal.valueOf(parsePrice(localization, data[5]));
                String currencySymbol = data[6];

                records.add(
                        new TsvOfferDTO(
                                localization, country, departureDate, arrivalDate, place, price, currencySymbol)
                );
            }
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
            throw new IOException("Failed to parse TSV file: " + fileDirectory + e);
        }
        return records;
    }

    public Locale localize(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Invalid locale format: null");
        }

        try {
            return LocaleFactory.fromString(text);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid locale format: " + text, e);
        }
    }

    public Date parseData(String text) {
        LocalDate localDate = LocalDate.parse(text);

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Double parsePrice(Locale localization, String text) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(localization);

        ParsePosition position = new ParsePosition(0);
        Number number = format.parse(text, position);
        if (number == null || position.getIndex() != text.length()) {
            throw new ParseException("Invalid price format: " + text, position.getIndex());
        }
        return number.doubleValue();
    }

    private String normalizePlaces(String place, Locale localization) {
        boolean isPlaceNullOrBlank = place == null || place.isBlank();
        if (isPlaceNullOrBlank) return "";

        String keyProperty = place.trim().toLowerCase(localization);

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