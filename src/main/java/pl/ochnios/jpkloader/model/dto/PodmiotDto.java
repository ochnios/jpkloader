package pl.ochnios.jpkloader.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PodmiotDto {

    @NotNull
    @Pattern(regexp = "^[1-9]((\\d[1-9])|([1-9]\\d))\\d{7}$")
    private String nip;

    @NotNull
    @Pattern(regexp = "^.{1,240}$")
    private String pelnaNazwa;

    @Pattern(regexp = "^\\d{9}|\\d{14}|$")
    private String regon;

    @NotNull
    @Pattern(regexp = "^.{1,36}$")
    private String wojewodztwo;

    @NotNull
    @Pattern(regexp = "^.{1,36}$")
    private String powiat;

    @NotNull
    @Pattern(regexp = "^.{1,36}$")
    private String gmina;

    @Pattern(regexp = "^.{1,65}|$")
    private String ulica;

    @NotNull
    @Pattern(regexp = "^.{1,9}$")
    private String nrDomu;

    @Pattern(regexp = "^.{1,10}|$")
    private String nrLokalu;

    @NotNull
    @Pattern(regexp = "^.{1,56}$")
    private String miejscowosc;

    @NotNull
    @Pattern(regexp = "^.{1,8}$")
    private String kodPocztowy;

    @NotNull
    @Pattern(regexp = "^.{1,65}$")
    private String poczta;
}
