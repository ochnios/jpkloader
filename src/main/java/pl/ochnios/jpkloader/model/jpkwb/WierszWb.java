package pl.ochnios.jpkloader.model.jpkwb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "WierszeWb")
@Data
public class WierszWb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "naglowek_id", nullable = false)
    private NaglowekWb naglowek;

    @Column(nullable = false)
    private int numer;

    @NotNull
    private LocalDate dataOperacji;

    @NotNull
    @Column(length = 256)
    private String nazwaPodmiotu;

    @NotNull
    @Column(length = 256)
    private String opisOperacji;

    @NotNull
    @Column(precision = 16, scale = 2)
    private BigDecimal kwotaOperacji;

    @NotNull
    @Column(precision = 16, scale = 2)
    private BigDecimal saldoOperacji;
}