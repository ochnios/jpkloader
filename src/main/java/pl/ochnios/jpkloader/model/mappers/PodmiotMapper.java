package pl.ochnios.jpkloader.model.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.ochnios.jpkloader.model.dto.PodmiotDto;
import pl.ochnios.jpkloader.model.jpkwb.PodmiotWb;

@Mapper(componentModel = "spring")
public interface PodmiotMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = ".", source = ".")
    PodmiotWb mapToPodmiotWb(PodmiotDto podmiotDto);

    @Mapping(target = ".", source = ".")
    PodmiotDto mapToPodmiotDto(PodmiotWb podmiot);
}
