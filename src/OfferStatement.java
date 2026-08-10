import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;

public class OfferStatement implements AutoCloseable {
    private final PreparedStatement preparedStatement;
    private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


    public OfferStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    public void addBatch() throws SQLException {
        this.preparedStatement.addBatch();
    }

    public int[] executeBatch() throws SQLException {
        return this.preparedStatement.executeBatch();
    }

    public void setValues(OfferEntity offerEntity) throws SQLException {
        preparedStatement.setString(1, offerEntity.localization.toString());
        preparedStatement.setString(2, offerEntity.countryName);
        preparedStatement.setString(3, DATE_FORMAT.format(offerEntity.departureDate));
        preparedStatement.setString(4, DATE_FORMAT.format(offerEntity.arrivalDate));
        preparedStatement.setString(5, offerEntity.place);
        if (offerEntity.price == null) {
            preparedStatement.setNull(6, Types.DOUBLE);
        } else {
            preparedStatement.setDouble(6, offerEntity.price);
        }
        preparedStatement.setString(7, offerEntity.currencyCode);
    }

    @Override
    public void close() throws SQLException {
        this.preparedStatement.close();
    }
}
