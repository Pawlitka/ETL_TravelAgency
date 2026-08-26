package UtilityClass;

import java.util.Locale;

public class LocaleFactory {
    public static Locale fromString(String localeInString) {
        if (localeInString == null || !localeInString.contains("_")) {
            throw new IllegalArgumentException("Invalid locale format: " + localeInString);
        }

        String[] splitParts = localeInString.split("_");
        return new Locale(splitParts[0], splitParts[1]);
    }
}
