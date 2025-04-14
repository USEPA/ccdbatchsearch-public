package gov.epa.ccte.api.batchsearch.service;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Slf4j
public class SearchResultService {

    public static List<BatchSearchResult> getBatchSearchResults(HashMap<String, String> processedSearchWords , HashMap<String, List<SearchWithChemicalDetails>> dbResult) {

        List<BatchSearchResult> batchSearchResults = new ArrayList<>();
        Set<String> searchWords = processedSearchWords.keySet();

        log.debug("search words = {}", searchWords);
        log.debug("database keys = {}", dbResult.keySet());

        for (String searchWord : searchWords) {
            String processedSearchWord = processedSearchWords.get(searchWord);
            if (dbResult.containsKey(processedSearchWord)) {
                List<SearchWithChemicalDetails> detailsList = dbResult.get(processedSearchWord);

                log.debug("{} has {} db records", processedSearchWord, detailsList.size());

                for (SearchWithChemicalDetails details : detailsList) {
                    log.debug("search = {} data match {} ", searchWord, details);
                    batchSearchResults.add(new BatchSearchResult(searchWord,
                            details.getSearchMatch(),
                            details.getDtxsid(),
                            details.getDtxcid(),
                            details.getCasrn(),
                            details.getPreferredName()));
                }
            } else {
                batchSearchResults.add(new BatchSearchResult(searchWord, Validators.getDataNotFoundMsg(searchWord),
                        "", "", "", ""));
            }
        }

        return batchSearchResults;
    }

    // This will remove duplicates(same dtxsid number) from search result
    public static List<SearchWithChemicalDetails> removeDuplicatesForLong(List<SearchWithChemicalDetails> chemicals) {

        log.debug("records = {}", chemicals.size());

        List<SearchWithChemicalDetails> returnList = new ArrayList<SearchWithChemicalDetails>();
        List<String> dtxsidList = new ArrayList<String>();
        List<String> dtxcidList = new ArrayList<String>();

        for (SearchWithChemicalDetails chemical : chemicals) {
            if (chemical.getDtxsid() != null) {
                if (dtxsidList.contains(chemical.getDtxsid()) == false) {
                    dtxsidList.add(chemical.getDtxsid());
                    returnList.add(chemical);
                } else {
                    log.debug("skip duplicate -  " + chemical);
                }
            }
        }
        return returnList;
    }

}
