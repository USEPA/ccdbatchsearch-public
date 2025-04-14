package gov.epa.ccte.api.batchsearch.service.searchtypes;

import gov.epa.ccte.api.batchsearch.domain.*;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class InChIKeySkeletonSearch implements SearchType {

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
        toxrefBatchSearchRepository = null;
        propertiesRepository= null;

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
    public List<ToxvalBatchSearch> getToxvalBatchSearch() {
        log.debug("dtxsids={}", dtxsids);
        return toxvalBatchSearchRepository.findByDtxsidInOrderByDtxsid(dtxsids);
    }

    @Override
    public List<ChemicalSynonym> getChemicalSynonyms() {
        return synonymRepository.findByDtxsidInAndDtxsidIsNot(dtxsids, "DTXSID00000000");
    }


    @Override
    public List<ChemlistToSubtance> getChemlistToSubstance() {
        return chemlistToSubtanceRepository.findByDtxsidIn(dtxsidDtxcidList);
    }

    @Override
    public List<ChemicalDetails> getChemicals(BatchSearchForm formBean) {

        List<String> searchNames = Arrays.asList("InChIKey", "Indigo InChIKey");

        String inchikeys = Arrays.stream(formBean.getSearchItems().split("\n"))
                .map(String::trim)
                .map(e -> e.split("-")[0])
                .collect(Collectors.joining("|"));

        return detailsRepository.startWithInChIKeySkeleton("(" + inchikeys + ")");
    }

    @Override
    public List<ChemicalProperties> getChemicalProperties() {
        log.debug("dtxsids={}", dtxsids);

        return propertiesRepository.findByDtxsidIn(dtxsids);
    }

    @Override
    public List<ToxrefBatchSearch> getToxrefBatchSearch() {
        log.debug("dtxsids={}", dtxsids);
        return toxrefBatchSearchRepository.findByDtxsidInOrderByDtxsid(dtxsids);
    }


    @Override
    public HashMap<String, List<SearchWithChemicalDetails>> getResults(BatchSearchForm formBean) {

        // updating processedSearchWords for result API
        for(String key : formBean.getSearchItems().split("\n")){
            processedSearchWords.put(key, key.split("-")[0]);
        }

        List<String> searchNames = Arrays.asList("InChIKey", "Indigo InChIKey");

        String inchikeys = Arrays.stream(formBean.getSearchItems().split("\n"))
                .map(String::trim)
                .map(e -> e.split("-")[0])
                .collect(Collectors.joining("|"));

        HashMap<String, List<SearchWithChemicalDetails>> results = new HashMap<>();

        String inchikey = "";
        List<SearchWithChemicalDetails> duplicates = new ArrayList<>();
        int i = 0;

        for (SearchWithChemicalDetails details : searchWithChemicalDetailsRepository.startWithInChIKeySkeleton("(" + inchikeys + ")")) {
            String in = details.getInchikey();
            String temp = details.getInchikey().split("-")[0];
            i++;
            if (inchikey.equals("")) {
                duplicates.add(details);
                inchikey = temp;
            } else if (inchikey.equals(temp)) {
                duplicates.add(details);
            } else {
                results.put(inchikey, duplicates);
                duplicates = new ArrayList<>();
                duplicates.add(details);
                inchikey = temp;
            }
            // for key building
            dtxsidDtxcidList.add(SearchType.buildKey(details.getDtxsid(), details.getDtxcid()));
            if(!details.getDtxsid().equals("DTXSID00000000"))
                dtxsids.add(details.getDtxsid());
        }
        // last record
        results.put(inchikey, duplicates);

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
        this.toxvalBatchSearchRepository=toxvalBatchSearchRepository;
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
