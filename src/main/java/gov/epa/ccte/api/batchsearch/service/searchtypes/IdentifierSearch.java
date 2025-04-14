package gov.epa.ccte.api.batchsearch.service.searchtypes;

import gov.epa.ccte.api.batchsearch.domain.*;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import gov.epa.ccte.api.batchsearch.service.Validators;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class IdentifierSearch implements SearchType {

    private SearchWithChemicalDetailsRepository searchWithChemicalDetailsRepository;
    private ChemicalDetailsRepository detailsRepository;
    private ChemlistToSubtanceRepository chemlistToSubtanceRepository;
    private ChemicalSynonymRepository synonymRepository;
    private RelatedSubstanceRepository relatedSubstanceRepository;
    private BioactivityAssayListRepository assayListRepository;
    private ToxvalBatchSearchRepository toxvalBatchSearchRepository;
    private ToxrefBatchSearchRepository toxrefBatchSearchRepository;
    private ChemicalPropertiesRepository propertiesRepository;


    private HashMap<String, String> processedSearchWords;
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
        toxrefBatchSearchRepository=null;
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
        log.debug("chemical lists for {} ", dtxsids);
        return chemlistToSubtanceRepository.findByDtxsidIn(dtxsids);
    }

    @Override
    public List<ChemicalProperties> getChemicalProperties() {
        log.debug("dtxsids={}", dtxsids);

        return propertiesRepository.findByDtxsidIn(dtxsids);
    }

    @Override
    public List<ChemicalDetails> getChemicals(BatchSearchForm formBean) {
        log.debug("form={}", formBean);

        List<String> searchWords = Arrays.asList(formBean.getSearchItems().split("\n"));
        log.debug("{} search words", searchWords.size());

        List<String> identifierTypes = Arrays.asList(formBean.getIdentifierTypes());
        log.debug("identifiers = {}", identifierTypes);

        List<String> searchNames = getSearchNames(identifierTypes);
        log.debug("search name = {}", searchNames);

        processedSearchWords = preprocess(identifierTypes, searchWords);
        log.debug("search words={}", processedSearchWords.values());

        //return SearchResultService.removeDuplicatesForLong(searchWithChemicalDetailsRepository.getChemicaDetails(processedSearchWords.values(), searchNames));
        return detailsRepository.searchChemicals(processedSearchWords.values(), searchNames);

    }

    @Override
    public HashMap<String, List<SearchWithChemicalDetails>> getResults(BatchSearchForm formBean) {

        List<String> searchWords = Arrays.asList(formBean.getSearchItems().split("\n"));
        List<String> identifierTypes = Arrays.asList(formBean.getIdentifierTypes());

        List<String> searchNames = getSearchNames(identifierTypes);

        processedSearchWords = preprocess(identifierTypes, searchWords);

        HashMap<String, List<SearchWithChemicalDetails>> results = new HashMap<>();
        List<SearchWithChemicalDetails> duplicates = new ArrayList<>();
        String searchWord = "";

        log.debug("search item = {}", processedSearchWords.keySet());
        log.debug("search processed items = {}", processedSearchWords.values());
        log.debug("search name = {}", searchNames);

        List<SearchWithChemicalDetails> qryResult = searchWithChemicalDetailsRepository.getIdentifierResult(processedSearchWords.values(), searchNames);

        //log.info("qryResult.size = {} ", qryResult.size());

        for (SearchWithChemicalDetails details : qryResult) {

            if (searchWord.equals("")) {
                duplicates.add(details);
                searchWord = details.getModifiedValue();
            } else if (searchWord.equalsIgnoreCase(details.getModifiedValue())) {
                duplicates.add(details);
            } else {
                log.debug("{} has {} records.", searchNames, duplicates.size());

                results.put(searchWord, duplicates);
                duplicates = new ArrayList<>(); //
                duplicates.add(details);
                searchWord = details.getModifiedValue();
            }

            // for key building
            dtxsidDtxcidList.add(SearchType.buildKey(details.getDtxsid(), details.getDtxcid()));
            if(!details.getDtxsid().equals("DTXSID00000000"))
                dtxsids.add(details.getDtxsid());
        }
        // last record
        results.put(searchWord, duplicates);

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


    private HashMap<String, String> preprocess(List<String> identifierTypes, List<String> searchWords) {

        HashMap<String, String> processedWords = new HashMap<String, String>();

        for (String searchWord : searchWords) {
            if (identifierTypes.contains("INCHIKEY") && Validators.isInchiKey(searchWord)) {
                log.debug("INCHIKEY = {}", searchWord);
                processedWords.put(searchWord, searchWord);
            } else if (identifierTypes.contains("CASRN")) {
                String temp = searchWord.replace(";", "").replaceAll("-", "");

                if (temp.contains("O")) {
                    // 75-O5-8 Letter O instead of 0
                    String temp2 = searchWord.replaceAll("-", "").replaceAll("O", "0");

                    if (Validators.isNumeric(temp2)) {
                        temp = temp2;
                    }
                } else if (temp.contains("/")) {
                    // 3/1/4860 Date Format: Should be 4860-03-1
                    String temp3 = searchWord.replaceAll("/", "");
                    if (Validators.isNumeric(temp3)) {
                        String[] splitCasrn = searchWord.split("/");
                        String temp4 = splitCasrn[2] + String.format("%02d", Integer.parseInt(splitCasrn[0])) + splitCasrn[1];
                        temp = temp4;
                    }
                }

                // casrn without dashes
                if (Validators.isNumeric(temp) && temp.length() >= 3) {
                    // remove leading zeros
                    String casrn = Validators.toCasrn(StringUtils.stripStart(temp,"0"));
                    log.debug("{} convert into casrn = {}", searchWord, casrn);
                    processedWords.put(searchWord, casrn);
                } else
                    processedWords.put(searchWord, processNonCasrnSearchWord(searchWord));
            } else {
                processedWords.put(searchWord, processNonCasrnSearchWord(searchWord));
            }
        }

        return processedWords;
    }

    private String processNonCasrnSearchWord(String searchWord) {
        String processedWord;

        if(Validators.isECNumber(searchWord)){
            processedWord = searchWord;
            log.debug("{} is EC Number", processedWord);
        }else{
            processedWord = searchWord;

            // From https://confluence.epa.gov/display/CCTEA/Search+Requirements
            // Make all character upper case
            processedWord = processedWord.toUpperCase();
            // Search word should be trim
            processedWord = processedWord.trim();

            processedWord = processedWord.replaceAll("-", " ");
        }

        return processedWord;
    }

    private List<String> getSearchNames(List<String> identifierTypes) {

        List<String> searchNames = new ArrayList<>();

        List<String> searchMatchForChemicalName = Arrays.asList("Approved Name", "Synonym", "Systematic Name", "Integrated Source Name",
                "Expert Validated Synonym", "Synonym from Valid Source", "FDA CAS-Like Identifier", "EHCA Number", "EC Number");
        List<String> searchMatchForCasrn = Arrays.asList("Deleted CAS-RN", "Alternate CAS-RN", "CASRN", "Integrated Source CAS-RN");
        List<String> searchMatchForInchikey = Arrays.asList("InChIKey", "Indigo InChIKey");

        for (String inputtype : identifierTypes) {

            if (inputtype.equalsIgnoreCase("chemical_name")) {
                searchNames.addAll(searchMatchForChemicalName);
            } else if (inputtype.equalsIgnoreCase("casrn")) {
                searchNames.addAll(searchMatchForCasrn);
            } else if (inputtype.equalsIgnoreCase("inchikey")) {
                searchNames.addAll(searchMatchForInchikey);
            } else if (inputtype.equalsIgnoreCase("dtxsid")) {
                searchNames.add("DSSTox_Substance_Id");
            }
        }

        return searchNames;
    }
}
