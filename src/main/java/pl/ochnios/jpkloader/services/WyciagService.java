package pl.ochnios.jpkloader.services;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.jpkloader.model.dto.NaglowekDto;
import pl.ochnios.jpkloader.model.dto.ServiceResponse;
import pl.ochnios.jpkloader.model.dto.WyciagDto;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;
import pl.ochnios.jpkloader.model.mappers.WyciagMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WyciagService {

    private final Validator validator;
    private final WyciagMapper wyciagMapper;

    public ServiceResponse<Iterable<WyciagDto>> uploadWyciagi(List<NaglowekWb> naglowki) {

        List<WyciagDto> wyciagi = new ArrayList<>();
        for (NaglowekWb naglowekWb : naglowki) {
            wyciagi.add(wyciagMapper.mapToWyciagDto(naglowekWb, 1));
        }

        return ServiceResponse.success(wyciagi);
    }

    private StringBuilder validateNaglowki(List<NaglowekDto> naglowekDtos) {
        StringBuilder validationErrors = new StringBuilder();
        for (var naglowekDto : naglowekDtos) {
            Set<ConstraintViolation<NaglowekDto>> violations = validator.validate(naglowekDto);
            if (!violations.isEmpty()) {
                validationErrors.append("Nagłówek dla rachunku ").append(naglowekDto.getNumerRachunku()).append(": ");
                for (var violation : violations) {
                    validationErrors.append(violation.getPropertyPath()).append(" ")
                            .append(violation.getMessage()).append(", ");
                }
            }
        }
        return validationErrors;
    }
}
