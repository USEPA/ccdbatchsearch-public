package gov.epa.ccte.api.batchsearch.service.filegenerators.csv;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.IdentifierSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

public class IdentifierToCsv extends CsvBase implements ExportType {

    private IdentifierSearch identifierSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        HashMap<String, List<SearchWithChemicalDetails>> results = identifierSearch.getResults(searchForm);

        HashMap<String, String> processedSearchWords = identifierSearch.getProcessedSearchName();

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = identifierSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        addHeader(new String[]{"INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"}, searchForm.getDownloadItems(),selectedChemicalLists);

        // rest of contents
        for (String searchWord : searchForm.getSearchItems().split("\n")) {
            String key = processedSearchWords.get(searchWord);
            if (results.containsKey(key)) {
                List<SearchWithChemicalDetails> detailsList = results.get(key);
                for (SearchWithChemicalDetails details : detailsList) {
                    //csv.writeRow(parseDataForRow(details, searchForm.getDownloadItems()));
                    addDataRow(searchWord, details,selectedChemicalLists);
                }
            } else {
                //csv.writeRow(parseDataForErrorRow(searchWord, searchForm.getDownloadItems().length));
                addNotFoundRow(searchWord);

/*                batchSearchResults.add(new BatchSearchResult(searchWord, chemicalService.getDataNotFoundMsg(searchWord, inputTypes),
                        "","","", null));*/
            }
        }

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.identifierSearch = (IdentifierSearch) searchType;
        this.searchForm = searchForm;
    }

    @Override
    public MediaType getContentType() {
        return getCsvContentType();
    }

    @Override
    public String getFilename() {
        return getCsvFilename();
    }
}
