package gov.epa.ccte.api.batchsearch.repository;

import gov.epa.ccte.api.batchsearch.domain.ChemicalProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ChemicalPropertiesRepository extends JpaRepository<ChemicalProperties, Integer> {

    List<ChemicalProperties> findByDtxsidIn(Set<String> dtxsids);

}
