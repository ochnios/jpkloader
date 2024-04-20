package pl.ochnios.jpkloader.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;

@Repository
public interface NaglowekWbRepository extends CrudRepository<NaglowekWb, Integer> {

    Iterable<NaglowekWb> findAllByOrderByModifiedDesc();
}
