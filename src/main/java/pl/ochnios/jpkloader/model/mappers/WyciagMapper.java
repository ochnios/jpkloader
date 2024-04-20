package pl.ochnios.jpkloader.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.jpkloader.model.dto.WyciagDto;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;

@Mapper(componentModel = "spring")
public interface WyciagMapper {

    @Mapping(target = ".", source = ".")
    @Mapping(target = "rachunek", source = "naglowekWb.rachunek.numer")
    @Mapping(target = "nip", source = "naglowekWb.rachunek.podmiot.nip")
    @Mapping(target = "wiersze", source = "wiersze")
    WyciagDto mapToWyciagDto(NaglowekWb naglowekWb, int wiersze);
}
