package Repository;

import Database.Database;
import Entity.OfferEntity;
import Mapper.OfferMapper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class OfferRepository {
    private Connection connection;
    private final Database database;

    public OfferRepository(Database database) {
        this.database = database;
    }

    public List<OfferEntity> getAllOffers() {
        List<OfferEntity> offers = new ArrayList<>();

        String selectSQL = "SELECT * FROM offers";
        try {
            database.connect();
            try (Statement statement = database.connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(selectSQL)
            ) {
                while (resultSet.next()) {
                    OfferEntity offer = OfferMapper.toEntity(resultSet);
                    offers.add(offer);
                }
            }
        } catch (Exception e) {
            System.out.println("Offer repository error message: " + e);
        }
        return offers;
    }
}
