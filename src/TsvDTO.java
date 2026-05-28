import java.util.Date;
import java.util.Locale;

public class TsvDTO {
  public Locale localization;
  public String country;
  public Date departureDate;
  public Date arrivalDate;
  public String place;
  public Double price;
  public String currencySymbol;

  public TsvDTO(
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
