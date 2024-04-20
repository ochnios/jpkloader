package pl.ochnios.jpkloader.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ochnios.jpkloader.model.dto.NaglowekDto;
import pl.ochnios.jpkloader.model.dto.ServiceResponse;
import pl.ochnios.jpkloader.model.dto.WierszDto;
import pl.ochnios.jpkloader.model.dto.WyciagDto;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;
import pl.ochnios.jpkloader.model.jpkwb.WierszWb;
import pl.ochnios.jpkloader.model.mappers.NaglowekMapper;
import pl.ochnios.jpkloader.model.mappers.WierszMapper;
import pl.ochnios.jpkloader.model.mappers.WyciagMapper;
import pl.ochnios.jpkloader.repos.NaglowekWbRepository;
import pl.ochnios.jpkloader.repos.WierszWbRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WyciagService {

    private final RachunekService rachunekService;
    private final NaglowekWbRepository naglowekWbRepository;
    private final WierszWbRepository wierszWbRepository;
    private final NaglowekMapper naglowekMapper;
    private final WierszMapper wierszMapper;
    private final WyciagMapper wyciagMapper;

    @Transactional
    public ServiceResponse<Iterable<WyciagDto>> uploadWyciagi(List<NaglowekDto> naglowekDtos,
                                                              List<WierszDto> wierszDtos) {
        var naglowki = new ArrayList<NaglowekWb>(naglowekDtos.size());
        var wiersze = new ArrayList<WierszWb>(wierszDtos.size());

        var wierszeTmp = new ArrayList<>(wierszDtos.stream().map(wierszMapper::mapToWierszWb).toList());
        for (NaglowekDto naglowekDto : naglowekDtos) {
            var naglowek = naglowekMapper.mapToNaglowekWb(naglowekDto);
            var wierszeNaglowka = getWierszeForNaglowek(naglowek, wierszeTmp);

            if (wierszeNaglowka.isEmpty()) {
                return ServiceResponse.fail("Brak wierszy dla " + getNaglowekSummary(naglowek));
            }
            if (!checkSaldoIntegrity(naglowek, wierszeNaglowka)) {
                return ServiceResponse.fail("Niezgodne salda nagłówka i wierszy dla " + getNaglowekSummary(naglowek));
            }

            var rachunekResponse = rachunekService.createRachunek(naglowekDto.getNumerRachunku(),
                    naglowekDto.getNip(), naglowekDto.getDomyslnyKodWaluty());
            if (!rachunekResponse.isSuccess()) {
                return ServiceResponse.fail(rachunekResponse.getMessage());
            }

            naglowek.setRachunek(rachunekResponse.getData());
            naglowek.setWiersze(wierszeNaglowka);
            final AtomicInteger counter = new AtomicInteger(1);
            wierszeNaglowka.forEach(wiersz -> {
                wiersz.setNaglowek(naglowek);
                wiersz.setNumer(counter.getAndIncrement());
            });

            naglowki.add(naglowek);
            wiersze.addAll(wierszeNaglowka);
            wierszeTmp.removeAll(wierszeNaglowka);
        }

        if (!wierszeTmp.isEmpty()) {
            return ServiceResponse.fail("Istnieją wiersze bez nagłówków " + wierszeTmp);
        }

        naglowekWbRepository.saveAll(naglowki);
        wierszWbRepository.saveAll(wiersze);

        return ServiceResponse.success(getAllWyciagi().getData());
    }

    public ServiceResponse<Iterable<WyciagDto>> getAllWyciagi() {
        var naglowki = naglowekWbRepository.findAllByOrderByIdDesc();
        var wyciagi = StreamSupport.stream(naglowki.spliterator(), false).map(
                n -> wyciagMapper.mapToWyciagDto(n, n.getWiersze().size())).collect(Collectors.toList()
        );
        return ServiceResponse.success(wyciagi);
    }

    private List<WierszWb> getWierszeForNaglowek(NaglowekWb naglowekWb, List<WierszWb> wiersze) {
        return wiersze.stream().filter(
                w -> w.getNaglowek().getRachunek().getNumer().equals(naglowekWb.getRachunek().getNumer())
                        && w.getDataOperacji().isAfter(naglowekWb.getDataOd())
                        && w.getDataOperacji().isBefore(naglowekWb.getDataDo())
        ).toList();
    }

    private boolean checkSaldoIntegrity(NaglowekWb naglowekWb, List<WierszWb> wiersze) {
        var first = wiersze.get(0);
        var last = wiersze.get(wiersze.size() - 1);
        return naglowekWb.getSaldoPoczatkowe().equals(first.getSaldoOperacji().subtract(first.getKwotaOperacji()))
                && naglowekWb.getSaldoKoncowe().equals(last.getSaldoOperacji());
    }

    private String getNaglowekSummary(NaglowekWb naglowekWb) {
        return naglowekWb.getRachunek().getPodmiot().getNip() + " "
                + naglowekWb.getRachunek().getNumer() + " "
                + naglowekWb.getDataOd() + " - " + naglowekWb.getDataOd();
    }
}
