package Mapper;

import Entity.OfferEntity;
import TSV.TsvDTO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TsvMapper {
    private TsvMapper() {
    }

    public static OfferEntity toEntity(TsvDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Input parameter dto cannot be null.");
        }

        return new OfferEntity(
                dto.localization(),
                dto.countryName(),
                dto.departureDate(),
                dto.arrivalDate(),
                dto.place(),
                dto.price(),
                dto.currencyCode()
        );
    }

    public static List<OfferEntity> toEntity(List<TsvDTO> dtoList) {
        if (dtoList == null) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .filter(Objects::nonNull)
                .map(TsvMapper::toEntity)
                .collect(Collectors.toList());
    }
}
