package pl.ochnios.jpkloader.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.Podmiot;

import java.util.Optional;

@Repository
public interface PodmiotRepository extends CrudRepository<Podmiot, Integer> {

    Optional<Podmiot> findAllByNip(String nip);

    Iterable<Podmiot> findAllByOrderByIdDesc();
}
