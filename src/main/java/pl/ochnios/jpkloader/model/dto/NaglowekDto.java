package pl.ochnios.jpkloader.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaglowekDto {

    @NotNull
    @Pattern(regexp = "^[1-9]((\\d[1-9])|([1-9]\\d))\\d{7}$")
    private String nip;

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[0-9A-Z]{10,30}$")
    private String numerRachunku;

    @NotNull
    @Pattern(regexp = "^(\\d{4})-(\\d{2})-(\\d{2})$")
    private String dataOd;

    @NotNull
    @Pattern(regexp = "^(\\d{4})-(\\d{2})-(\\d{2})$")
    private String dataDo;

    @NotNull
    @Pattern(regexp = "^[A-Z]{3}$")
    private String domyslnyKodWaluty;

    @NotNull
    @Pattern(regexp = "^\\d{1,16}(\\.\\d{2})?$")
    private String saldoPoczatkowe;

    @NotNull
    @Pattern(regexp = "^\\d{1,16}(\\.\\d{2})?$")
    private String saldoKoncowe;

    @NotNull
    @Pattern(regexp = "^\\d{4}$")
    private String kodUrzedu;
}
