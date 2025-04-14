package gov.epa.ccte.api.batchsearch.service.filegenerators.csv;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.InChIKeySkeletonSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

public class InChIKeySkeletonToCsv extends CsvBase implements ExportType {

    private InChIKeySkeletonSearch inChIKeySkeletonSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        HashMap<String, List<SearchWithChemicalDetails>> results = inChIKeySkeletonSearch.getResults(searchForm);

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = inChIKeySkeletonSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        addHeader(new String[]{"INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"}, searchForm.getDownloadItems(),selectedChemicalLists);

        for (String word : searchForm.getSearchItems().split("\n")) {
            String inchikey = word.split("-")[0];
            if (results.containsKey(inchikey)) {
                List<SearchWithChemicalDetails> detailsList = results.get(inchikey);
                for (SearchWithChemicalDetails details : detailsList) {
                    //csv.writeRow(parseDataForRow(details, searchForm.getDownloadItems()));
                    addDataRow(word, details,selectedChemicalLists);
                }
            } else {
                //csv.writeRow(parseDataForErrorRow(word, searchForm.getDownloadItems().length));
                addNotFoundRow(word);
            }
        }

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.inChIKeySkeletonSearch = (InChIKeySkeletonSearch) searchType;
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
