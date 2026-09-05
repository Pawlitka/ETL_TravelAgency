package Mapper;

import Entity.OfferEntity;
import UtilityClass.LocaleFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class OfferMapper {
    public static OfferEntity toEntity(ResultSet resultSet) throws SQLException {
        String localeLabel = resultSet.getString("locale");
        Locale localization = LocaleFactory.fromString(localeLabel);

        return new OfferEntity(
                localization,
                resultSet.getString("country_name"),
                resultSet.getDate("departure_date"),
                resultSet.getDate("arrival_date"),
                resultSet.getString("place"),
                resultSet.getObject("price", Double.class),
                resultSet.getString("currency_code")
        );
    }
}
