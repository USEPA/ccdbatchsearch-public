package gov.epa.ccte.api.batchsearch.service.searchtypes;

import gov.epa.ccte.api.batchsearch.domain.*;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DtxcidSearch implements SearchType {

    private SearchWithChemicalDetailsRepository searchWithChemicalDetailsRepository;
    private ChemicalDetailsRepository detailsRepository;
    private ChemlistToSubtanceRepository chemlistToSubtanceRepository;
    private ChemicalSynonymRepository synonymRepository;
    private RelatedSubstanceRepository relatedSubstanceRepository;
    private BioactivityAssayListRepository assayListRepository;
    private ToxvalBatchSearchRepository toxvalBatchSearchRepository;
    private ToxrefBatchSearchRepository toxrefBatchSearchRepository;
    private ChemicalPropertiesRepository propertiesRepository;


    private HashMap<String, String> processedSearchName = new HashMap<>(); // this is used but result endpoint
    private Set<String> dtxsidDtxcidList = new HashSet<>();
    private Set<String> dtxsids = new HashSet<>();

    @Override
    public void initialize() {
        searchWithChemicalDetailsRepository= null;
        detailsRepository= null;
        chemlistToSubtanceRepository= null;
        synonymRepository= null;
        relatedSubstanceRepository= null;
        assayListRepository= null;
        toxvalBatchSearchRepository=null;
        propertiesRepository= null;
        toxrefBatchSearchRepository = null;

        processedSearchName = new HashMap<>(); // this is used but result endpoint
        dtxsidDtxcidList = new HashSet<>();
        dtxsids = new HashSet<>();

    }

    @Override
    public List<String> getAssaysList() {
        return assayListRepository.getAssaysList();
    }

    @Override
    public List<BioactivityAssayList> getAssayResults() {
        log.debug("dtxsids={}", dtxsids);
        return assayListRepository.getAssayResults(dtxsids);
    }

    @Override
    public List<RelatedSubstance> getRelatedSubstance() {
        log.debug("dtxsids={}", dtxsids);
        return relatedSubstanceRepository.findByDtxsidInOrderByDtxsid(dtxsids);
    }

    @Override
    public List<ChemicalSynonym> getChemicalSynonyms() {
        log.debug("dtxsids={}", dtxsids);
        return synonymRepository.findByDtxsidInAndDtxsidIsNot(dtxsids, "DTXSID00000000");
    }

    @Override
    public List<ToxvalBatchSearch> getToxvalBatchSearch() {
        log.debug("dtxsids={}", dtxsids);
        return toxvalBatchSearchRepository.findByDtxsidInOrderByDtxsid(dtxsids);
    }

    @Override
    public List<ToxrefBatchSearch> getToxrefBatchSearch() {
        log.debug("dtxsids={}", dtxsids);
        return toxrefBatchSearchRepository.findByDtxsidInOrderByDtxsid(dtxsids);
    }

    @Override
    public List<ChemlistToSubtance> getChemlistToSubstance() {
        log.debug("dtxsids={}", dtxsids);

        return chemlistToSubtanceRepository.findByDtxsidIn(dtxsids);
    }

    @Override
    public List<ChemicalProperties> getChemicalProperties() {
        log.debug("dtxsids={}", dtxsids);

        return propertiesRepository.findByDtxsidIn(dtxsids);
    }

    @Override
    public List<ChemicalDetails> getChemicals(BatchSearchForm formBean) {
        List<String> searchWords = Arrays.asList(formBean.getSearchItems().split("\n"));

        List<String> searchNames = Arrays.asList("DSSTox_Compound_Id");

        log.debug("searchName = {} , searchWords={}", searchNames, searchWords);
        List<ChemicalDetails> result = detailsRepository.searchDtxcid(searchWords);

        log.debug("result size = {}", result.size());

        return result;
    }

    @Override
    public HashMap<String, List<SearchWithChemicalDetails>> getResults(BatchSearchForm formBean) {
        List<String> dtxcids = Arrays.asList(formBean.getSearchItems().split("\n"));

        // updating processedSearchName for result endpoint
        for (String dtxcid: dtxcids)
            processedSearchName.put(dtxcid, dtxcid);

        List<String> searchNames = Arrays.asList("DSSTox_Compound_Id");

        log.debug("searchName = {} , dtxcids={}", searchNames, dtxcids);

        HashMap<String, List<SearchWithChemicalDetails>> results = new HashMap<>();

        // for each matched dtxcid
        for (SearchWithChemicalDetails details : searchWithChemicalDetailsRepository.searchDtxcid(dtxcids)) {
            results.put(details.getModifiedValue(), Arrays.asList(details));

            dtxsidDtxcidList.add(SearchType.buildKey(details.getDtxsid(), details.getDtxcid()));
            if(details.getDtxsid() != null && !details.getDtxsid().equals("DTXSID00000000"))
                dtxsids.add(details.getDtxsid());
        }

        return results;

    }

    @Override
    public void setRepository(SearchWithChemicalDetailsRepository searchWithChemicalDetailsRepository,
                              ChemicalDetailsRepository detailsRepository,
                              ChemlistToSubtanceRepository chemlistToSubtanceRepository,
                              ChemicalSynonymRepository synonymRepository,
                              RelatedSubstanceRepository relatedSubstanceRepository, BioactivityAssayListRepository assayListRepository,
                              ToxvalBatchSearchRepository toxvalBatchSearchRepository,
                              ToxrefBatchSearchRepository toxrefBatchSearchRepository,
                              ChemicalPropertiesRepository propertiesRepository) {
        this.searchWithChemicalDetailsRepository = searchWithChemicalDetailsRepository;
        this.detailsRepository= detailsRepository;
        this.chemlistToSubtanceRepository = chemlistToSubtanceRepository;
        this.synonymRepository = synonymRepository;
        this.relatedSubstanceRepository = relatedSubstanceRepository;
        this.assayListRepository = assayListRepository;
        this.toxvalBatchSearchRepository=toxvalBatchSearchRepository;
        this.toxrefBatchSearchRepository=toxrefBatchSearchRepository;
        this.propertiesRepository = propertiesRepository;
    }

    @Override
    public HashMap<String, String> getProcessedSearchName() {
        return processedSearchName;
    }

    @Override
    public Set<String> getChemicalList(String[] chemicalLists) {

        return SearchChemicalService.getChemicalList(chemicalLists,dtxsids);
    }

    @Override
    public void getDhsdata() {

        String [] dtxsidsArray = dtxsids.toArray(new String[0]);

        SearchChemicalService.getGhsData(dtxsidsArray);
    }

}
