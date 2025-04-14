package gov.epa.ccte.api.batchsearch.repository;

import gov.epa.ccte.api.batchsearch.domain.BioactivityAssayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Set;

@RepositoryRestResource(exported = false)
public interface BioactivityAssayListRepository extends JpaRepository<BioactivityAssayList, String> {

    @Query(value = "select assay_component_endpoint_name from {h-schema}bioactivity_assay_list group by assay_component_endpoint_name order by assay_component_endpoint_name ",
            nativeQuery = true)
    List<String> getAssaysList();

    @Query (value = "select id, dtxsid, assay_component_endpoint_name, ac50 from {h-schema}bioactivity_assay_list a " +
            "where a.dtxsid in (:dtxsids) "
            , nativeQuery = true)
    List<BioactivityAssayList> getAssayResults(@Param("dtxsids") Set<String> dtxsids);

}
