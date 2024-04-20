package pl.ochnios.jpkloader.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.jpkloader.model.dto.NaglowekDto;
import pl.ochnios.jpkloader.model.dto.ServiceResponse;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;
import pl.ochnios.jpkloader.model.mappers.NaglowekMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NaglowekService {

    private final CsvSchema csvSchema;
    private final CsvMapper csvMapper;
    private final Validator validator;
    private final NaglowekMapper naglowekMapper;

    public ServiceResponse<List<NaglowekWb>> loadNaglowki(MultipartFile naglowkiCsv) {
        if (naglowkiCsv.isEmpty()) {
            return ServiceResponse.fail("Plik z nagłówkami jest pusty");
        }

        List<NaglowekDto> naglowekDtos;
        try {
            MappingIterator<NaglowekDto> readValues = csvMapper.readerFor(NaglowekDto.class).with(csvSchema)
                    .readValues(naglowkiCsv.getInputStream());
            naglowekDtos = readValues.readAll();
        } catch (Exception ex) {
            return ServiceResponse.fail("Błąd podczas odczytu nagłówków: " + ex.getMessage());
        }

        var validationErrors = validateNaglowki(naglowekDtos);
        if (!validationErrors.isEmpty()) {
            return ServiceResponse.fail("Wystąpiły błędy walidacji nagłówków: " + validationErrors);
        }

        List<NaglowekWb> naglowki = naglowekDtos.stream()
                .map(naglowekMapper::mapToNaglowekWb)
                .collect(Collectors.toList());

        return ServiceResponse.success(naglowki);
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
