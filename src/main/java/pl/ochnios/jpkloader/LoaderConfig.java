package pl.ochnios.jpkloader;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoaderConfig {

    @Bean
    public CsvSchema csvSchema() {
        return CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
    }

    @Bean
    public CsvMapper csvMapper() {
        return new CsvMapper();
    }
}
