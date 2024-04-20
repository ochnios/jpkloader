package pl.ochnios.jpkloader.model.jpkwb;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "WierszeWb")
@Data
public class WierszWb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "naglowek_id")
    private NaglowekWb naglowek;

    @Column(nullable = false)
    private int numer;

    @Column(nullable = false)
    private LocalDate dataOperacji;

    @Nationalized
    @Column(nullable = false, length = 256)
    private String nazwaPodmiotu;

    @Nationalized
    @Column(nullable = false, length = 256)
    private String opisOperacji;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal kwotaOperacji;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal saldoOperacji;
}