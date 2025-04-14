package gov.epa.ccte.api.batchsearch.service.searchtypes;

import gov.epa.ccte.api.batchsearch.domain.*;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class MassSearch implements SearchType {

    private SearchWithChemicalDetailsRepository searchWithChemicalDetailsRepository;
    private ChemicalDetailsRepository detailsRepository;
    private ChemlistToSubtanceRepository chemlistToSubtanceRepository;
    private ChemicalSynonymRepository synonymRepository;
    private RelatedSubstanceRepository relatedSubstanceRepository;
    private BioactivityAssayListRepository assayListRepository;
    private ToxvalBatchSearchRepository toxvalBatchSearchRepository;
    private ToxrefBatchSearchRepository toxrefBatchSearchRepository;
    private ChemicalPropertiesRepository propertiesRepository;


    private HashMap<String, String> processedSearchWords = new HashMap<>();
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
        propertiesRepository=null;

        processedSearchWords = new HashMap<>(); // this is used but result endpoint
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
    public List<ChemicalDetails> getChemicals(BatchSearchForm formBean) {
        // this option is disable on UI, we don't need to develop it,
        return null;
    }

    @Override
    public List<ChemicalProperties> getChemicalProperties() {
        log.debug("dtxsids={}", dtxsids);

        return propertiesRepository.findByDtxsidIn(dtxsids);
    }


    @Override
    public HashMap<String, List<SearchWithChemicalDetails>> getResults(BatchSearchForm formBean) {

        HashMap<String, List<SearchWithChemicalDetails>> results = new HashMap<>();

        for (String massValues : formBean.getSearchItems().split("\n")) {
            // compose 200 +/- 5ppm
            String inputText = massValues + "+/-" + formBean.getMassError() + "ppm";
            Double diff = formBean.getMassError();
            Double inputMass = Double.parseDouble(massValues);
            Double error = inputMass * diff / 1000000;
            Double startMass = inputMass - error;
            Double endMass = inputMass + error;

            log.debug("input value = {} diff = {} start mass = {} end mass = {}", massValues, diff, startMass, endMass);

            List<SearchWithChemicalDetails> chemicals = searchWithChemicalDetailsRepository.getChemicalForMassRange(startMass, endMass);

            log.debug("For {} DB return {} records.", inputText, chemicals.size());

            // updating processedSearchWords for result API
            log.debug("input text = {}", inputText);

            processedSearchWords.put(inputText, inputText);

            results.put(inputText, chemicals);

            // for key building
            for(SearchWithChemicalDetails chemical : chemicals) {
                dtxsidDtxcidList.add(SearchType.buildKey(chemical.getDtxsid(), chemical.getDtxcid()));
                if(!chemical.getDtxsid().equals("DTXSID00000000"))
                    dtxsids.add(chemical.getDtxsid());
            }
        }

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
        return processedSearchWords;
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
