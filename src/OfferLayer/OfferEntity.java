package OfferLayer;

import java.util.Date;
import java.util.Locale;

public class OfferEntity {
    public final Locale localization;
    public final String countryName;
    public final Date departureDate;
    public final Date arrivalDate;
    public final String place;
    public final Double price;
    public final String currencyCode;

    public OfferEntity(
            Locale localization,
            String countryName,
            Date departureDate,
            Date arrivalDate,
            String place,
            Double price,
            String currencyCode) {
        this.localization = localization;
        this.countryName = countryName;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.place = place;
        this.price = price;
        this.currencyCode = currencyCode;
    }
}
