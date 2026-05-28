import java.util.Date;
import java.util.Locale;

public class OfferEntity {
  public Long id;
  public final Locale localization;
  public final String country;
  public final Date departureDate;
  public final Date arrivalDate;
  public final String place;
  public final Double price;
  public final String currencySymbol;

  public OfferEntity(
      Locale localization,
      String country,
      Date departureDate,
      Date arrivalDate,
      String place,
      Double price,
      String currencySymbol) {
    this.localization = localization;
    this.country = country;
    this.departureDate = departureDate;
    this.arrivalDate = arrivalDate;
    this.place = place;
    this.price = price;
    this.currencySymbol = currencySymbol;
  }
}
