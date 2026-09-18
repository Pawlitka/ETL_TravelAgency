package Entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

public record OfferEntity(Locale localization, String countryName, Date departureDate, Date arrivalDate, String place,
                          BigDecimal price, String currencyCode) {
}
