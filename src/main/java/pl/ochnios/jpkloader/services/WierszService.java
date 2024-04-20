package pl.ochnios.jpkloader.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.jpkloader.model.dto.ServiceResponse;
import pl.ochnios.jpkloader.model.dto.WierszDto;
import pl.ochnios.jpkloader.model.jpkwb.WierszWb;
import pl.ochnios.jpkloader.model.mappers.WierszMapper;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WierszService {

    private final CsvSchema csvSchema;
    private final CsvMapper csvMapper;
    private final Validator validator;
    private final WierszMapper wierszMapper;

    public ServiceResponse<List<WierszWb>> loadWiersze(MultipartFile wierszeCsv) {
        if (wierszeCsv.isEmpty()) {
            return ServiceResponse.fail("Plik z wierszami jest pusty");
        }

        List<WierszDto> wierszDtos;
        try {
            MappingIterator<WierszDto> readValues = csvMapper.readerFor(WierszDto.class).with(csvSchema)
                    .readValues(wierszeCsv.getInputStream());
            wierszDtos = readValues.readAll();
        } catch (Exception ex) {
            return ServiceResponse.fail("Błąd podczas odczytu wierszy: " + ex.getMessage());
        }

        var validationErrors = validateWiersze(wierszDtos);
        if (!validationErrors.isEmpty()) {
            return ServiceResponse.fail("Wystąpiły błędy walidacji wierszy: " + validationErrors);
        }

        List<WierszWb> wiersze = wierszDtos.stream().map(wierszMapper::mapToWierszWb).toList();

        return ServiceResponse.success(wiersze);
    }

    private StringBuilder validateWiersze(List<WierszDto> wierszDtos) {
        StringBuilder validationErrors = new StringBuilder();
        for (var wierszDto : wierszDtos) {
            Set<ConstraintViolation<WierszDto>> violations = validator.validate(wierszDto);
            if (!violations.isEmpty()) {
                validationErrors.append("Wiersz dla rachunku ").append(wierszDto.getNumerRachunku()).append(": ");
                for (var violation : violations) {
                    validationErrors.append(violation.getPropertyPath()).append(" ")
                            .append(violation.getMessage()).append(", ");
                }
            }
        }
        return validationErrors;
    }
}
