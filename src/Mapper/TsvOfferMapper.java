package Mapper;

import Entity.OfferEntity;
import TSV.TsvOfferDTO;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TsvOfferMapper {
    private TsvOfferMapper() {
    }

    public static OfferEntity toEntity(TsvOfferDTO dto) {
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

    public static List<OfferEntity> toEntity(List<TsvOfferDTO> dtoList) {
        if (dtoList == null) {
            return Collections.emptyList();
        }
        return dtoList.stream()
                .filter(Objects::nonNull)
                .map(TsvOfferMapper::toEntity)
                .collect(Collectors.toList());
    }
}
