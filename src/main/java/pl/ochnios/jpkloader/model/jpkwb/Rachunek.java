package pl.ochnios.jpkloader.model.jpkwb;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "Rachunki")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rachunek {

    @Id
    @Column(columnDefinition = "char(34)")
    private String numer;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(nullable = false, name = "podmiot_nip", referencedColumnName = "nip")
    private PodmiotWb podmiot;

    @ManyToOne
    @JoinColumn(nullable = false, name = "waluta_kod", referencedColumnName = "kod")
    private Waluta waluta;
}
