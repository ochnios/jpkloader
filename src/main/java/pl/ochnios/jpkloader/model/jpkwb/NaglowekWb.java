package pl.ochnios.jpkloader.model.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import pl.ochnios.jpkloader.model.jpkwb.Rachunek;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "NaglowkiWb")
@Data
public class NaglowekWb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "rachunek_numer", referencedColumnName = "numer")
    private Rachunek rachunek;

    @NotNull
    private LocalDate dataOd;

    @NotNull
    private LocalDate dataDo;

    @NotNull
    @Column(precision = 16, scale = 2)
    private BigDecimal saldoPoczatkowe;

    @NotNull
    @Column(precision = 16, scale = 2)
    private BigDecimal saldoKoncowe;

    @NotNull
    @Column(columnDefinition = "char(4)")
    private String kodUrzedu;
}
