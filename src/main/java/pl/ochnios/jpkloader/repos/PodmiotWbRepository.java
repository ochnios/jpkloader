package pl.ochnios.jpkloader.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.PodmiotWb;

import java.util.Optional;

@Repository
public interface PodmiotWbRepository extends CrudRepository<PodmiotWb, String> {

    Optional<PodmiotWb> findAllByNip(String nip);

    Iterable<PodmiotWb> findAllByOrderByModifiedDesc();
}
