package pl.ochnios.jpkloader.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.jpkloader.services.NaglowekService;
import pl.ochnios.jpkloader.services.WyciagService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WyciagController {

    private final NaglowekService naglowekService;
    private final WyciagService wyciagService;

    @GetMapping({"/", "/wyciagi"})
    public String wyciagi(Model model) {
        return "wyciagi";
    }

    @PostMapping(value = "wyciagi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadWyciagi(@RequestParam("naglowki") MultipartFile naglowkiCsv,
                                @RequestParam("wiersze") MultipartFile wierszeCsv,
                                Model model) {
        var naglowkiResponse = naglowekService.loadNaglowki(naglowkiCsv);
        if (naglowkiResponse.isSuccess()) {
            log.info("Loaded naglowki: " + naglowkiResponse);
            var wyciagiResponse = wyciagService.uploadWyciagi(naglowkiResponse.getData());
            if (wyciagiResponse.isSuccess()) {
                log.info("Loaded wyciagi: " + wyciagiResponse);
                model.addAttribute("wyciagiSuccess", "Dodano pomyślnie!");
                model.addAttribute("wyciagi", wyciagiResponse.getData());
            } else {
                log.error("Uploading wyciagi failed: " + wyciagiResponse);
                model.addAttribute("wyciagiFailed", "Błąd: " + wyciagiResponse.getMessage());
            }
        } else {
            log.error("Loading naglowki failed: " + naglowkiResponse);
            model.addAttribute("wyciagiFailed", "Błąd: " + naglowkiResponse.getMessage());
        }

        return "wyciagi";
    }
}
