package pl.ochnios.jpkloader.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.Waluta;

@Repository
public interface WalutaRepository extends CrudRepository<Waluta, String> {
}
