package pl.ochnios.jpkloader.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import pl.ochnios.jpkloader.services.NaglowekService;
import pl.ochnios.jpkloader.services.WierszService;
import pl.ochnios.jpkloader.services.WyciagService;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WyciagController {

    private final NaglowekService naglowekService;
    private final WierszService wierszService;
    private final WyciagService wyciagService;
    private final Transformer transformer;

    @GetMapping({"/", "/wyciagi"})
    public String wyciagi(Model model) {
        model.addAttribute("wyciagi", wyciagService.getAllWyciagi().getData());
        return "wyciagi";
    }

    @PostMapping(value = "wyciagi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadWyciagi(@RequestParam("naglowki") MultipartFile naglowkiCsv,
                                @RequestParam("wiersze") MultipartFile wierszeCsv,
                                Model model) {
        var naglowkiResponse = naglowekService.loadNaglowki(naglowkiCsv);
        var wierszeResponse = wierszService.loadWiersze(wierszeCsv);
        if (naglowkiResponse.isSuccess() && wierszeResponse.isSuccess()) {
            log.info("Loaded naglowki: " + naglowkiResponse);
            log.info("Loaded wiersze: " + wierszeResponse);
            var wyciagiResponse = wyciagService.uploadWyciagi(naglowkiResponse.getData(), wierszeResponse.getData());
            if (wyciagiResponse.isSuccess()) {
                log.info("Loaded wyciagi: " + wyciagiResponse);
                model.addAttribute("wyciagiSuccess", "Dodano pomyślnie!");
                model.addAttribute("wyciagi", wyciagiResponse.getData());
            } else {
                log.error("Uploading wyciagi failed: " + wyciagiResponse);
                model.addAttribute("wyciagiFailed", "Błąd: " + wyciagiResponse.getMessage());
            }
        } else {
            String message = "Błąd: ";
            if (!naglowkiResponse.isSuccess()) {
                log.error("Loading naglowki failed: " + naglowkiResponse);
                message += naglowkiResponse.getMessage();
            }
            if (!wierszeResponse.isSuccess()) {
                log.error("Loading wiersze failed: " + naglowkiResponse);
                message += wierszeResponse.getMessage();
            }
            model.addAttribute("wyciagiFailed", message);
        }
        return "wyciagi";
    }

    @GetMapping("/wyciagi/{filename}.xml")
    public void getWyciag(HttpServletResponse res, @PathVariable String filename) throws Exception {
        String idStr = filename.substring(filename.lastIndexOf('_') + 1);
        var xmlResponse = wyciagService.generateJpkWbXml(Integer.parseInt(idStr));
        Context ctx = new Context();
        if (xmlResponse.isSuccess()) {
            ctx.setVariable("data", prettifyXml(xmlResponse.getData()));
        } else {
            ctx.setVariable("data", "<err>" + xmlResponse.getMessage() + "</err>");
        }
        respondWithXml(res, "template", ctx, filename);
    }

    private void respondWithXml(HttpServletResponse res, String template,
                                Context ctx, String filename) throws IOException {
        String xml = xmlEngine().process(template, ctx);
        res.setHeader("Content-Disposition", "attachment; filename=" + filename + ".xml");
        res.setContentType("application/xml; charset=utf-8");
        PrintWriter writer = res.getWriter();
        writer.print(xml);
        writer.close();
    }

    private String prettifyXml(String input) throws TransformerException {
        StreamResult result = new StreamResult(new StringWriter());
        Source source = new StreamSource(new StringReader(input));
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    public SpringTemplateEngine xmlEngine() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(new AnnotationConfigApplicationContext());
        resolver.setPrefix("classpath:/xml/");
        resolver.setSuffix(".xml");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.XML);

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);

        return engine;
    }
}
