package pl.ochnios.jpkloader.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.jpkloader.model.dto.ServiceResponse;
import pl.ochnios.jpkloader.model.jpkwb.Rachunek;
import pl.ochnios.jpkloader.repos.PodmiotWbRepository;
import pl.ochnios.jpkloader.repos.RachunekRepository;
import pl.ochnios.jpkloader.repos.WalutaRepository;

@Service
@RequiredArgsConstructor
public class RachunekService {

    private final RachunekRepository rachunekRepository;
    private final PodmiotWbRepository podmiotWbRepository;
    private final WalutaRepository walutaRepository;

    @Transactional
    public ServiceResponse<Rachunek> createRachunek(String numer, String nip, String kodWaluty) {
        var waluta = walutaRepository.findById(kodWaluty);
        if (waluta.isEmpty()) {
            return ServiceResponse.fail(String.format("Brak waluty o kodzie %s", kodWaluty));
        }

        var current = rachunekRepository.findById(numer);
        if (current.isPresent()) {
            if (!current.get().getPodmiot().getNip().equals(nip)) {
                return ServiceResponse.fail(String.format("Rachunek %s już istnieje dla NIPu %s", numer, nip));
            }
            if (!current.get().getWaluta().getKod().equals(kodWaluty)) {
                return ServiceResponse.fail(String.format("Rachunek %s już istnieje z walutą %s",
                        numer, current.get().getWaluta().getKod()));
            }
            return ServiceResponse.success(current.get());
        } else {
            var podmiot = podmiotWbRepository.findById(nip);
            if (podmiot.isEmpty()) {
                return ServiceResponse.fail(String.format("Brak podmiotu o numerze NIP=%s", nip));
            }
            var rachunek = rachunekRepository.save(new Rachunek(numer, podmiot.get(), waluta.get()));
            return ServiceResponse.success(rachunek);
        }
    }
}
