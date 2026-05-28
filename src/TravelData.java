import java.io.File;
import java.io.IOException;

public class TravelData {
    private File fileDirectory;
    private TsvFileReader tsvFileReader;
    private TsvMapper tsvMapper;

    TravelData(File fileDirectory) throws IOException {
        tsvFileReader = new TsvFileReader(fileDirectory);
        if (fileDirectory == null || !fileDirectory.isDirectory()) return;

        File[] files = fileDirectory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) {
                tsvFileReader.readFile(file);
            }
        }
    }
}
