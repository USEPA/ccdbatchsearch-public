package gov.epa.ccte.api.batchsearch.service.searchtypes;

import gov.epa.ccte.api.batchsearch.domain.*;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MsReadyFormulaSearch implements SearchType {

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
        toxrefBatchSearchRepository = null;
        propertiesRepository=null;

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
        log.debug("dtxsidDtxcidList={}", dtxsidDtxcidList);

        return chemlistToSubtanceRepository.findByDtxsidIn(dtxsidDtxcidList);
    }

    @Override
    public List<ChemicalDetails> getChemicals(BatchSearchForm formBean) {
        log.debug("form bean = {}", formBean);

        //return searchWithChemicalDetailsRepository.getChemicalsForMsReadyFormula(formBean.getSearchItems().split("\n"));
        return detailsRepository.searchMsReadyFormula(formBean.getSearchItems().split("\n"));
    }

    @Override
    public List<ChemicalProperties> getChemicalProperties() {
        log.debug("dtxsids={}", dtxsids);

        return propertiesRepository.findByDtxsidIn(dtxsids);
    }

    @Override
    public HashMap<String, List<SearchWithChemicalDetails>> getResults(BatchSearchForm formBean) {

        // updating for result API
        for(String searchWord: formBean.getSearchItems().split("\n") )
            processedSearchName.put(searchWord, searchWord); // formula is use for indexing data

        HashMap<String, List<SearchWithChemicalDetails>> results = new HashMap<>();

        String formula = "";
        List<SearchWithChemicalDetails> duplicates = new ArrayList<>();

        for (SearchWithChemicalDetails details : searchWithChemicalDetailsRepository.getChemicalsForMsReadyFormula(formBean.getSearchItems().split("\n"))) {
            String temp = details.getSearchWord();
            if (formula.equals("")) {
                duplicates.add(details);
                formula = temp;
            } else if (formula.equals(temp)) {
                duplicates.add(details);
            } else {
                results.put(formula, duplicates);
                duplicates = new ArrayList<>();
                duplicates.add(details);
                formula = temp;
            }

            // for key building
            dtxsidDtxcidList.add(SearchType.buildKey(details.getDtxsid(), details.getDtxcid()));
            if(!details.getDtxsid().equals("DTXSID00000000"))
                dtxsids.add(details.getDtxsid());
        }
        // last record
        results.put(formula, duplicates);

        return results;
    }

    @Override
    public void setRepository(SearchWithChemicalDetailsRepository searchWithChemicalDetailsRepository,
                              ChemicalDetailsRepository detailsRepository,
                              ChemlistToSubtanceRepository chemlistToSubtanceRepository,
                              ChemicalSynonymRepository synonymRepository,
                              RelatedSubstanceRepository relatedSubstanceRepository, BioactivityAssayListRepository assayListRepository,
                              ToxvalBatchSearchRepository toxvalBatchSearchRepository, ToxrefBatchSearchRepository toxrefBatchSearchRepository,
                              ChemicalPropertiesRepository propertiesRepository) {
        this.searchWithChemicalDetailsRepository = searchWithChemicalDetailsRepository;
        this.detailsRepository= detailsRepository;
        this.chemlistToSubtanceRepository = chemlistToSubtanceRepository;
        this.synonymRepository = synonymRepository;
        this.relatedSubstanceRepository = relatedSubstanceRepository;
        this.assayListRepository = assayListRepository;
        this.toxvalBatchSearchRepository = toxvalBatchSearchRepository;
        this.toxrefBatchSearchRepository = toxrefBatchSearchRepository;
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
