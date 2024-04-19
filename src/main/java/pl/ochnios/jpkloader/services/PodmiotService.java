package pl.ochnios.jpkloader.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.jpkloader.model.dto.PodmiotDto;
import pl.ochnios.jpkloader.model.dto.ServiceResponse;
import pl.ochnios.jpkloader.model.jpkwb.PodmiotWb;
import pl.ochnios.jpkloader.model.mappers.PodmiotMapper;
import pl.ochnios.jpkloader.repos.PodmiotWbRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PodmiotService {

    private final CsvSchema csvSchema;
    private final CsvMapper csvMapper;
    private final Validator validator;
    private final PodmiotWbRepository podmiotWbRepository;
    private final PodmiotMapper podmiotMapper;

    public ServiceResponse<Iterable<PodmiotDto>> uploadPodmioty(MultipartFile podmiotyCsv) {
        if (podmiotyCsv.isEmpty()) {
            return ServiceResponse.fail("Provided file is empty");
        }

        List<PodmiotDto> podmiotDtos;
        try {
            MappingIterator<PodmiotDto> readValues = csvMapper.readerFor(PodmiotDto.class).with(csvSchema)
                    .readValues(podmiotyCsv.getInputStream());
            podmiotDtos = readValues.readAll();
        } catch (Exception ex) {
            return ServiceResponse.fail("Error while reading file: " + ex.getMessage());
        }

        StringBuilder validationErrors = new StringBuilder();
        for (var podmiotDto : podmiotDtos) {
            Set<ConstraintViolation<PodmiotDto>> violations = validator.validate(podmiotDto);
            if (!violations.isEmpty()) {
                validationErrors.append("Podmiot ").append(podmiotDto.getNip()).append(": ");
                for (var violation : violations) {
                    validationErrors.append(violation.getPropertyPath()).append(" ")
                            .append(violation.getMessage()).append(", ");
                }
            }
        }
        if (!validationErrors.isEmpty()) {
            return ServiceResponse.fail("Wystąpiły błędy walidacji: " + validationErrors);
        }

        for (PodmiotDto podmiotDto : podmiotDtos) {
            PodmiotWb podmiot = podmiotMapper.mapToPodmiotWb(podmiotDto);
            podmiotWbRepository.save(podmiot);
        }
        var podmioty = StreamSupport.stream(podmiotWbRepository.findAllByOrderByModifiedDesc().spliterator(), false)
                .map(podmiotMapper::mapToPodmiotDto).toList();

        return ServiceResponse.success(podmioty);
    }

    public ServiceResponse<Iterable<PodmiotWb>> getAllPodmioty() {
        return ServiceResponse.success(podmiotWbRepository.findAllByOrderByModifiedDesc());
    }
}
