package Mapper;

import OfferLayer.OfferEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class OfferMapper {
    public static OfferEntity toEntity(ResultSet resultSet) throws SQLException {
        String localeLabel = resultSet.getString("locale");
        String[] splitParts = localeLabel.split("_");
        Locale localization = new Locale(splitParts[0], splitParts[1]);

        return new OfferEntity(
                localization,
                resultSet.getString("country_name"),
                resultSet.getDate("departure_date"),
                resultSet.getDate("arrival_date"),
                resultSet.getString("place"),
                resultSet.getDouble("price"),
                resultSet.getString("currency_code")
        );
    }
}
