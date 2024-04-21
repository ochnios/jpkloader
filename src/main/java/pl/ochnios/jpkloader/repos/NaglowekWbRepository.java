package pl.ochnios.jpkloader.repos;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.ochnios.jpkloader.model.jpkwb.NaglowekWb;

@Repository
public interface NaglowekWbRepository extends CrudRepository<NaglowekWb, Integer> {

    Iterable<NaglowekWb> findAllByOrderByIdDesc();

    @Procedure(value = "GENERUJ_JPK_WB_XML", outputParameterName = "xml")
    String generateJpkWbXml(int naglowekId);
}
