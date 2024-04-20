package pl.ochnios.jpkloader.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.PodmiotWb;

@Repository
public interface PodmiotWbRepository extends CrudRepository<PodmiotWb, String> {

    Iterable<PodmiotWb> findAllByOrderByModifiedDesc();
}
