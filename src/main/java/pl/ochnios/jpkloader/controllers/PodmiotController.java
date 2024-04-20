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
import pl.ochnios.jpkloader.services.PodmiotService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PodmiotController {

    private final PodmiotService podmiotService;

    @GetMapping("/podmioty")
    public String podmioty(Model model) {
        model.addAttribute("podmioty", podmiotService.getAllPodmioty().getData());
        return "podmioty";
    }

    @PostMapping(value = "/podmioty", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadPodmioty(@RequestParam("podmioty") MultipartFile podmiotyCsv, Model model) {
        var response = podmiotService.uploadPodmioty(podmiotyCsv);
        if (response.isSuccess()) {
            log.info("Uploaded: " + response);
            model.addAttribute("podmiotySuccess", "Dodano pomyślnie!");
            model.addAttribute("podmioty", response.getData());
        } else {
            log.error("Failed: " + response);
            model.addAttribute("podmiotyFailed", "Błąd: " + response.getMessage());
        }
        return "podmioty";
    }
}
