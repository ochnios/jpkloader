package pl.ochnios.jpkloader.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.Rachunek;

@Repository
public interface RachunekRepository extends CrudRepository<Rachunek, String> {
}
