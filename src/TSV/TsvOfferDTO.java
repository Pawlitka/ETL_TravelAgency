package TSV;

import java.util.Date;
import java.util.Locale;

public record TsvOfferDTO(Locale localization, String countryName, Date departureDate, Date arrivalDate, String place,
                          Double price, String currencyCode) {
}
