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
  private File fileDirectory;
  private static final Integer LINE_LENGTH = 7;

  public TsvFileReader(File fileDirectory) throws IOException {
    this.fileDirectory = fileDirectory;
    readFile(fileDirectory);
  }

  public List<TsvDTO> readFile(File fileDirectory) throws IOException {
    List<TsvDTO> records = new ArrayList<>();
    try {
      BufferedReader br = new BufferedReader(new FileReader(fileDirectory));
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split("\t");
        if (data.length >= LINE_LENGTH) {
          String[] localizationParts = data[0].split("_");
          Locale localization = Locale.of(localizationParts[0], localizationParts[1]);

          String country = data[1];

          LocalDate localDeparture = LocalDate.parse(data[2]);
          LocalDate localArrival = LocalDate.parse(data[3]);

          Date departureDate =
              Date.from(localDeparture.atStartOfDay(ZoneId.systemDefault()).toInstant());
          Date arrivalDate =
              Date.from(localArrival.atStartOfDay(ZoneId.systemDefault()).toInstant());

          String place = data[4];

          NumberFormat format = NumberFormat.getInstance(localization);
          Number number = format.parse(data[5]);
          Double price = number.doubleValue();

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
}
