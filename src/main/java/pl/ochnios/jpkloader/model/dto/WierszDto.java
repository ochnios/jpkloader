package pl.ochnios.jpkloader.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WierszDto {

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[0-9A-Z]{10,30}$")
    private String numerRachunku;

    @NotNull
    @Pattern(regexp = "^(\\d{4})-(\\d{2})-(\\d{2})$")
    private String dataOperacji;

    @NotNull
    @Pattern(regexp = "^.{1,36}$")
    private String nazwaPodmiotu;

    @NotNull
    @Pattern(regexp = "^.{1,256}$")
    private String opisOperacji;

    @NotNull
    @Pattern(regexp = "^[+-]?\\d{1,16}(\\.\\d{2})?$")
    private String kwotaOperacji;

    @NotNull
    @Pattern(regexp = "^[+-]?\\d{1,16}(\\.\\d{2})?$")
    private String saldoOperacji;
}
