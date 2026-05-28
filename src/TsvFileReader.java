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
    private static final Integer LINE_LENGTH = 7;
    private final List<TsvDTO> records = new ArrayList<>();
    private File fileDirectory;

    public TsvFileReader(File fileDirectory) throws IOException {
        this.fileDirectory = fileDirectory;
    }

    public TsvFileReader() {
    }

    public List<TsvDTO> readFile(File fileDirectory) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileDirectory));
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\t");
                if (data.length >= LINE_LENGTH) {
                    Locale localization = localizationConvert(data[0]);

                    String country = data[1];


                    Date departureDate = dateParser(data[2]);
                    Date arrivalDate = dateParser(data[3]);

                    String place = data[4];
                    Double price = priceParser(localization, data[5]);
                    String currencySymbol = data[6];

                    records.add(
                            new TsvDTO(
                                    localization, country, departureDate, arrivalDate, place, price, currencySymbol));
                    break;
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("Err");
        }
        return records;
    }

    public Locale localizationConvert(String data) {
        String[] localizationParts = data.split("_");
        return Locale.of(localizationParts[0], localizationParts[1]);
    }

    public Date dateParser(String date) {
        LocalDate localDate = LocalDate.parse(date);


        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public Double priceParser(Locale localization, String data) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(localization);
        Number number = format.parse(data);
        return number.doubleValue();
    }

    public List<TsvDTO> getRecords() {
        return records;
    }
}