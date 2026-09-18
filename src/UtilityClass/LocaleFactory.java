package UtilityClass;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocaleFactory {
    public static Locale fromString(String localeInString) {
        boolean isLocaleNull = localeInString == null;
        if (isLocaleNull) {
            throw new IllegalArgumentException("Invalid locale format: " + null);
        }

        Pattern pattern = Pattern.compile("[a-z]{2}_[A-Z]{2}");
        Matcher matcher = pattern.matcher(localeInString);

        String[] splitParts = localeInString.split("_", -1);

        boolean hasInvalidLengthOrValuesAreBlank = splitParts.length != 2 || splitParts[0].isBlank() || splitParts[1].isBlank() || !matcher.matches();
        if (hasInvalidLengthOrValuesAreBlank) {
            throw new IllegalArgumentException("Invalid locale format: " + localeInString);
        }

        return new Locale(splitParts[0], splitParts[1]);
    }
}
