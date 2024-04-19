package pl.ochnios.jpkloader.model.jpkwb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "Rachunki")
@Data
public class Rachunek {

    @Id
    @Column(columnDefinition = "char(34)")
    private String numer;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "podmiot_nip", referencedColumnName = "nip")
    private PodmiotWb podmiot;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "waluta_kod", referencedColumnName = "kod")
    private Waluta waluta;
}
