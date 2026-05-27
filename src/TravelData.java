import java.io.File;
import java.io.IOException;

public class TravelData {
  private File fileDirectory;
  TsvFileReader tsvFileReader;

  TravelData(File fileDirectory) throws IOException {
    this.fileDirectory = fileDirectory;
    this.tsvFileReader = new TsvFileReader(fileDirectory);
  }
}
