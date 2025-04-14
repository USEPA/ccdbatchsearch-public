package gov.epa.ccte.api.batchsearch.repository;

import gov.epa.ccte.api.batchsearch.domain.ToxvalBatchSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Set;

@RepositoryRestResource(exported = false)
public interface ToxvalBatchSearchRepository extends JpaRepository<ToxvalBatchSearch, Integer> {

    List<ToxvalBatchSearch> findByDtxsidInOrderByDtxsid(Set<String> dtxsids);
}
