package pl.ochnios.jpkloader.services;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.jpkloader.model.dto.ServiceResponse;
import pl.ochnios.jpkloader.model.dto.WyciagDto;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;
import pl.ochnios.jpkloader.model.jpkwb.WierszWb;
import pl.ochnios.jpkloader.model.mappers.WyciagMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WyciagService {

    private final Validator validator;
    private final WyciagMapper wyciagMapper;

    public ServiceResponse<Iterable<WyciagDto>> uploadWyciagi(List<NaglowekWb> naglowki, List<WierszWb> wiersze) {
        List<WyciagDto> wyciagi = new ArrayList<>();
        for (NaglowekWb naglowekWb : naglowki) {
            String rachunek = naglowekWb.getRachunek().getNumer();
            int wierszy = (int) wiersze.stream()
                    .filter(w -> w.getNaglowek().getRachunek().getNumer().equals(rachunek)).count();
            wyciagi.add(wyciagMapper.mapToWyciagDto(naglowekWb, wierszy));
        }
        return ServiceResponse.success(wyciagi);
    }
}
