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
import pl.ochnios.jpkloader.model.jpkwb.PodmiotWb;
import pl.ochnios.jpkloader.repos.PodmiotWbRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PodmiotService {

    private final CsvSchema csvSchema;
    private final CsvMapper csvMapper;
    private final Validator validator;
    private final PodmiotWbRepository podmiotWbRepository;

    public ServiceResponse<Iterable<PodmiotWb>> uploadPodmioty(MultipartFile podmiotyCsv) {
        if (podmiotyCsv.isEmpty()) {
            return ServiceResponse.fail("Provided file is empty");
        }

        List<PodmiotWb> podmioty;
        try {
            MappingIterator<PodmiotWb> readValues = csvMapper.readerFor(PodmiotWb.class).with(csvSchema)
                    .readValues(podmiotyCsv.getInputStream());
            podmioty = readValues.readAll();
        } catch (Exception ex) {
            return ServiceResponse.fail("Error while reading file: " + ex.getMessage());
        }

        StringBuilder validationErrors = new StringBuilder();
        for (var podmiot : podmioty) {
            Set<ConstraintViolation<PodmiotWb>> violations = validator.validate(podmiot);
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
            var found = podmiotWbRepository.findAllByNip(podmiot.getNip());
            found.ifPresent(value -> podmiot.setId(value.getId()));
            podmiotWbRepository.save(podmiot);
        }

        return ServiceResponse.success(podmiotWbRepository.findAllByOrderByIdDesc());
    }

    public ServiceResponse<Iterable<PodmiotWb>> getAllPodmioty() {
        return ServiceResponse.success(podmiotWbRepository.findAllByOrderByIdDesc());
    }
}
