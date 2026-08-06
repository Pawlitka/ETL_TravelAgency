import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TsvFileReader {
    private static final Integer NUMBER_OF_VALUES_PER_LINE = 7;
    private static final String TABULATOR = "\t";
    private final List<TsvDTO> records = new ArrayList<>();
    private File fileDirectory;

    public TsvFileReader(File fileDirectory) throws IOException {
        this.fileDirectory = fileDirectory;
    }

    public TsvFileReader() {
    }

    public List<TsvDTO> readFile(File fileDirectory) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileDirectory));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(TABULATOR);
                if (data.length >= NUMBER_OF_VALUES_PER_LINE) {
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
            e.printStackTrace();
        } finally {
            br.close();
        }
        return records;
    }

    public Locale localize(String text) {
        String[] localizationParts = text.split("_");
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
        if (place == null) return "";
        String toLowerCase = place.toLowerCase().trim();
        String language = localization.getLanguage();

        return switch (language) {
            case "pl" -> switch (toLowerCase) {
                case "morze" -> "place_name.sea";
                case "jezioro" -> "place_name.lake";
                case "góry" -> "place_name.mountains";
                default -> place;
            };
            case "de" -> switch (toLowerCase) {
                case "meer" -> "place_name.sea";
                case "see" -> "place_name.lake";
                case "gebirge" -> "place_name.mountains";
                default -> place;
            };
            case "en" -> switch (toLowerCase) {
                case "sea" -> "place_name.sea";
                case "lake" -> "place_name.lake";
                case "mountains" -> "place_name.mountains";
                default -> place;
            };
            default -> place;
        };
    }
}