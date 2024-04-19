package pl.ochnios.jpkloader.model.jpkwb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PodmiotyWb")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PodmiotWb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 10)
    private String nip;

    @Column(nullable = false, length = 240)
    private String pelnaNazwa;

    @Column(length = 14)
    private String regon;

    @Column(nullable = false, length = 14)
    private String wojewodztwo;

    @Column(nullable = false, length = 36)
    private String powiat;

    @Column(nullable = false, length = 36)
    private String gmina;

    @Column(length = 65)
    private String ulica;

    @Column(nullable = false, length = 9)
    private String nrDomu;

    @Column(length = 10)
    private String nrLokalu;

    @Column(nullable = false, length = 56)
    private String miejscowosc;

    @Column(nullable = false, length = 8)
    private String kodPocztowy;

    @Column(nullable = false, length = 65)
    private String poczta;
}
