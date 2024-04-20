package pl.ochnios.jpkloader.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.jpkloader.model.dto.WierszDto;
import pl.ochnios.jpkloader.model.jpkwb.WierszWb;

@Mapper(componentModel = "spring")
public interface WierszMapper {

    @Mapping(target = ".", source = ".")
    @Mapping(target = "naglowek.rachunek.numer", source = "numerRachunku")
    WierszWb mapToWierszWb(WierszDto wierszDto);
}
