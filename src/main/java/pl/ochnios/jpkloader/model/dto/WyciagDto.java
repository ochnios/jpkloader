package pl.ochnios.jpkloader.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WyciagDto {

    private int id;
    private String nip;
    private String rachunek;
    private String dataOd;
    private String dataDo;
    private int wiersze;
}
