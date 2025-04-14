package gov.epa.ccte.api.batchsearch.repository;

import gov.epa.ccte.api.batchsearch.domain.ChemicalSynonym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Set;

@RepositoryRestResource(exported = false)
public interface ChemicalSynonymRepository extends JpaRepository<ChemicalSynonym, String> {

    List<ChemicalSynonym> findByDtxsidInAndDtxsidIsNot(Set<String> dtxsids, String dtxsid);
}
