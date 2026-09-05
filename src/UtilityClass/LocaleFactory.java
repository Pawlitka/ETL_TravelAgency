package UtilityClass;

import java.util.Locale;

public class LocaleFactory {
    public static Locale fromString(String localeInString) {
        boolean isLocaleNull = localeInString == null;
        if (isLocaleNull) {
            throw new IllegalArgumentException("Invalid locale format: " + null);
        }

        String[] splitParts = localeInString.split("_", -1);

        boolean hasInvalidLengthOrValuesAreBlank = splitParts.length != 2 || splitParts[0].isBlank() || splitParts[1].isBlank();
        if (hasInvalidLengthOrValuesAreBlank) {
            throw new IllegalArgumentException("Invalid locale format: " + localeInString);
        }

        return new Locale(splitParts[0], splitParts[1]);
    }
}
