package pl.ochnios.jpkloader.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.jpkloader.model.dto.NaglowekDto;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;

@Mapper(componentModel = "spring")
public interface NaglowekMapper {

    @Mapping(target = ".", source = ".")
    @Mapping(target = "rachunek.numer", source = "numerRachunku")
    @Mapping(target = "rachunek.podmiot.nip", source = "nip")
    @Mapping(target = "rachunek.waluta.kod", source = "domyslnyKodWaluty")
    NaglowekWb mapToNaglowekWb(NaglowekDto naglowekDto);
}
