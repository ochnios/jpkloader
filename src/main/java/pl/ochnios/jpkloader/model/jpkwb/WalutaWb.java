package pl.ochnios.jpkloader.model.jpkwb;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "WalutyWb")
@Data
public class WalutaWb {

    @Id
    @Column(nullable = false, length = 3)
    private String kod;

    @Nationalized
    @Column(length = 50)
    private String kraj;

    @Nationalized
    @Column(length = 50)
    private String nazwa;
}
