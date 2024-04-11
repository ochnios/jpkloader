package pl.ochnios.jpkloader.services;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.jpkloader.model.ServiceResponse;
import pl.ochnios.jpkloader.model.jpkwb.Podmiot;
import pl.ochnios.jpkloader.repos.PodmiotRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PodmiotService {

    private final CsvSchema csvSchema;
    private final CsvMapper csvMapper;
    private final Validator validator;
    private final PodmiotRepository podmiotRepository;

    public ServiceResponse<Iterable<Podmiot>> uploadPodmioty(MultipartFile podmiotyCsv) {
        if (podmiotyCsv.isEmpty()) {
            return ServiceResponse.fail("Provided file is empty");
        }

        List<Podmiot> podmioty;
        try {
            MappingIterator<Podmiot> readValues = csvMapper.readerFor(Podmiot.class).with(csvSchema)
                    .readValues(podmiotyCsv.getInputStream());
            podmioty = readValues.readAll();
        } catch (Exception ex) {
            return ServiceResponse.fail("Error while reading file: " + ex.getMessage());
        }

        StringBuilder validationErrors = new StringBuilder();
        for (var podmiot : podmioty) {
            Set<ConstraintViolation<Podmiot>> violations = validator.validate(podmiot);
            if (!violations.isEmpty()) {
                validationErrors.append("Podmiot ").append(podmiot.getNip()).append(": ");
                for (var violation : violations) {
                    validationErrors.append(violation.getPropertyPath()).append(" ")
                            .append(violation.getMessage()).append(", ");
                }
            }
        }
        if (!validationErrors.isEmpty()) {
            return ServiceResponse.fail("Wystąpiły błędy walidacji: " + validationErrors);
        }

        for (var podmiot : podmioty) {
            var found = podmiotRepository.findAllByNip(podmiot.getNip());
            found.ifPresent(value -> podmiot.setId(value.getId()));
            podmiotRepository.save(podmiot);
        }

        return ServiceResponse.success(podmiotRepository.findAllByOrderByIdDesc());
    }

    public ServiceResponse<Iterable<Podmiot>> getAllPodmioty() {
        return ServiceResponse.success(podmiotRepository.findAllByOrderByIdDesc());
    }
}
