package pl.ochnios.jpkloader.model.jpkwb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "PodmiotyWb")
@Data
public class PodmiotWb {

    @Id
    @Column(columnDefinition = "char(10)")
    private String nip;

    @Nationalized
    @Column(nullable = false, length = 240)
    private String pelnaNazwa;

    @Column(length = 14)
    private String regon;

    @Nationalized
    @Column(nullable = false, length = 14)
    private String wojewodztwo;

    @Nationalized
    @Column(nullable = false, length = 36)
    private String powiat;

    @Nationalized
    @Column(nullable = false, length = 36)
    private String gmina;

    @Nationalized
    @Column(length = 65)
    private String ulica;

    @Column(nullable = false, length = 9)
    private String nrDomu;

    @Column(length = 10)
    private String nrLokalu;

    @Nationalized
    @Column(nullable = false, length = 56)
    private String miejscowosc;

    @Column(nullable = false, length = 8)
    private String kodPocztowy;

    @Nationalized
    @Column(nullable = false, length = 65)
    private String poczta;

    @UpdateTimestamp
    private Instant modified;
}
