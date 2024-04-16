package pl.ochnios.jpkloader.model.jpkwb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "PodmiotyWb")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PodmiotWb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    @Size(min = 10, max = 10)
    private String nip;

    @Column(nullable = false)
    @Size(min = 1, max = 240)
    private String pelnaNazwa;

    @Size(min = 9, max = 14)
    private String regon;

    @Column(nullable = false)
    @Size(min = 1, max = 36)
    private String wojewodztwo;

    @Column(nullable = false)
    @Size(min = 1, max = 36)
    private String powiat;

    @Column(nullable = false)
    @Size(min = 1, max = 36)
    private String gmina;

    @Size(min = 1, max = 65)
    private String ulica;

    @Column(nullable = false)
    @Size(min = 1, max = 9)
    private String nrDomu;

    @Size(min = 1, max = 10)
    private String nrLokalu;

    @Column(nullable = false)
    @Size(min = 1, max = 56)
    private String miejscowosc;

    @Column(nullable = false)
    @Size(min = 1, max = 8)
    private String kodPocztowy;

    @Column(nullable = false)
    @Size(min = 1, max = 65)
    private String poczta;
}
