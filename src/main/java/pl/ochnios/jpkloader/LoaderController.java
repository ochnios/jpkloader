package pl.ochnios.jpkloader;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Controller
public class LoaderController {

    @GetMapping("/")
    public String index() {
        return "uploadFiles";
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFiles(@RequestParam("header") MultipartFile header,
                              @RequestParam("rows") MultipartFile rows,
                              Model model) {
        if (header.isEmpty() || rows.isEmpty()) {
            model.addAttribute("mergedText", "Please upload two not empty files.");
            return "uploadFiles";
        }

        try {
            StringBuilder mergedText = new StringBuilder();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(header.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(rows.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader1.readLine()) != null) {
                mergedText.append(line).append("\n");
            }
            while ((line = reader2.readLine()) != null) {
                mergedText.append(line).append("\n");
            }
            model.addAttribute("mergedText", mergedText.toString());
        } catch (Exception e) {
            model.addAttribute("mergedText", "Error occurred: " + e.getMessage());
        }

        return "uploadFiles";
    }
}
