package pl.ochnios.jpkloader.model.jpkwb;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "NaglowkiWb")
@Data
public class NaglowekWb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "rachunek_numer", referencedColumnName = "numer")
    private Rachunek rachunek;

    @OneToMany(mappedBy = "naglowek")
    private List<WierszWb> wiersze;

    @Column(nullable = false)
    private LocalDate dataOd;

    @Column(nullable = false)
    private LocalDate dataDo;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal saldoPoczatkowe;

    @Column(nullable = false, precision = 16, scale = 2)
    private BigDecimal saldoKoncowe;

    @Column(nullable = false, columnDefinition = "char(4)")
    private String kodUrzedu;
}
