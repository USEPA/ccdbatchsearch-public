package gov.epa.ccte.api.batchsearch.repository;

import gov.epa.ccte.api.batchsearch.domain.RelatedSubstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@RepositoryRestResource(exported = false)
public interface RelatedSubstanceRepository extends JpaRepository<RelatedSubstance, BigInteger> {

    List<RelatedSubstance> findByDtxsidInOrderByDtxsid(Set<String> dtxsids);
}
