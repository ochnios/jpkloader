package pl.ochnios.jpkloader.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.jpkloader.services.PodmiotService;

import java.io.IOException;


@Controller
@RequiredArgsConstructor
public class PodmiotController {

    private final PodmiotService loaderService;

    @GetMapping("/podmioty")
    public String podmioty(Model model) {
        model.addAttribute("podmioty", loaderService.getAllPodmioty().getData());
        return "podmioty";
    }

    @PostMapping(value = "/podmioty", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadPodmioty(@RequestParam("podmioty") MultipartFile podmiotyCsv, Model model) throws IOException {
        var response = loaderService.uploadPodmioty(podmiotyCsv);
        if (response.isSuccess()) {
            model.addAttribute("podmiotySuccess", "Dodano pomyślnie!");
            model.addAttribute("podmioty", response.getData());
        } else {
            model.addAttribute("podmiotyFailed", "Wystąpiły błędy: " + response.getMessage());
        }
        return "podmioty";
    }
}
