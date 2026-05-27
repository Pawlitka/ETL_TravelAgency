import java.util.Date;
import java.util.Locale;

public record TsvDTO(
    Locale localization,
    String country,
    Date departureDate,
    Date arrivalDate,
    String place,
    Double price,
    String currencySymbol) {}
