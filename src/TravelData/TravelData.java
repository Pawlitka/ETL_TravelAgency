package TravelData;

import Entity.OfferEntity;
import Mapper.TsvOfferMapper;
import TSV.TsvFileReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TravelData {
    private List<OfferEntity> offers = new ArrayList<>();

    public TravelData(File fileDirectory) {
        boolean isNotValidDirectory = fileDirectory == null || !fileDirectory.isDirectory();

        if (isNotValidDirectory) {
            throw new IllegalArgumentException("Data directory does not exist or is not a directory: " + fileDirectory);
        }

        File[] files = fileDirectory.listFiles();
        boolean isNullOrInvalidLength = files == null || files.length == 0;
        if (isNullOrInvalidLength) return;

        TsvFileReader tsvFileReader = new TsvFileReader(fileDirectory);

        this.offers = Arrays.stream(files)
                .filter(File::isFile)
                .map(file -> {
                    try {
                        return tsvFileReader.readFile(file);// Convert to unchecked
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(TsvOfferMapper::toEntity)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public String translation(String country, Locale source, Locale target) {
        return Arrays.stream(Locale.getAvailableLocales())
                .filter(loc -> loc.getDisplayCountry(source).equalsIgnoreCase(country))
                .map(loc -> loc.getDisplayCountry(target))
                .findFirst()
                .orElse(country);
    }

    public List<OfferEntity> getOffers() {
        return offers;
    }
}
