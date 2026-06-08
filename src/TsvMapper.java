import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TsvMapper {
    public TsvMapper() {
    }

    public static OfferEntity toEntity(final TsvDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Input parameter dto cannot be null.");
        }

        return new OfferEntity(
                dto.localization,
                dto.country,
                dto.departureDate,
                dto.arrivalDate,
                dto.place,
                dto.price,
                dto.currencySymbol
        );
    }

    public static List<OfferEntity> toEntity(final List<TsvDTO> dtoList) {
        if (dtoList == null) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .filter(Objects::nonNull)
                .map(TsvMapper::toEntity)
                .collect(Collectors.toList());
    }
}
