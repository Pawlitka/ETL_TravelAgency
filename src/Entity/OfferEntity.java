package Entity;

import java.util.Date;
import java.util.Locale;

public record OfferEntity(Locale localization, String countryName, Date departureDate, Date arrivalDate, String place,
                          Double price, String currencyCode) {
}
