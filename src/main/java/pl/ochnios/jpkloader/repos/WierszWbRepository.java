package pl.ochnios.jpkloader.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.WierszWb;

@Repository
public interface WierszWbRepository extends CrudRepository<WierszWb, Integer> {
}
