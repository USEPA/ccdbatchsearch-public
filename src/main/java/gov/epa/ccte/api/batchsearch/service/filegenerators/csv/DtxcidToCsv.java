package gov.epa.ccte.api.batchsearch.service.filegenerators.csv;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.DtxcidSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class DtxcidToCsv extends CsvBase implements ExportType {

    private DtxcidSearch dtxcidSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        log.trace("start - exporting dtxcid csv data ");
        HashMap<String, List<SearchWithChemicalDetails>> results = dtxcidSearch.getResults(searchForm);

        log.debug("result = {} ", results.size());

        HashMap<String, String> processedSearchWords = dtxcidSearch.getProcessedSearchName();

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = dtxcidSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        addHeader(new String[]{"INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"}, searchForm.getDownloadItems(),selectedChemicalLists);

        // rest of contents
        for (String searchWord : processedSearchWords.keySet()) {
            if (results.containsKey(searchWord)) {
                // Here we only have one record per key, so get the top record only
                SearchWithChemicalDetails details = results.get(searchWord).get(0);

                //csv.writeRow(parseDataForRow(details, searchForm.getDownloadItems()));
                addDataRow(searchWord, details,selectedChemicalLists);
            } else {
                //csv.writeRow(parseDataForErrorRow(searchWord, searchForm.getDownloadItems().length));
                addNotFoundRow(searchWord);
            }
        }

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.dtxcidSearch = (DtxcidSearch) searchType;
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
