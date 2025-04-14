package gov.epa.ccte.api.batchsearch.service.filegenerators.sdf;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.ExactFormulaSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ExactFormulaToSdf extends SdfBase implements ExportType {

    private ExactFormulaSearch exactFormulaSearch;
    private BatchSearchForm searchForm;

    protected Set<String> chemlistLookup;
    protected String[] selectedChemicalLists;

    @Override
    public byte[] export() {
        HashMap<String, List<SearchWithChemicalDetails>> chemicals = exactFormulaSearch.getResults(searchForm);
        HashMap<String, String> processedSearchWords = exactFormulaSearch.getProcessedSearchName();

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = exactFormulaSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
            String[] combine = Stream.of(new String[]{"INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"},
                            searchForm.getDownloadItems(),selectedChemicalLists)
                    .flatMap(Stream::of).toArray(String[]::new);

            addMissingChemicals(searchForm, processedSearchWords, chemicals);

            return buildSDFfileWithChemicalLists(chemicals, combine, searchForm.getMolVersion(),chemlistLookup,selectedChemicalLists).toString().getBytes();

        }else {
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;


            String[] combine = Stream.of(new String[]{"INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"},
                            searchForm.getDownloadItems())
                    .flatMap(Stream::of).toArray(String[]::new);

            addMissingChemicals(searchForm, processedSearchWords, chemicals);

            return buildSDFfile(chemicals, combine, searchForm.getMolVersion()).toString().getBytes();
        }

    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.exactFormulaSearch = (ExactFormulaSearch) searchType;
        this.searchForm = searchForm;

    }

    @Override
    public MediaType getContentType() {
        return getSdfContentType();
    }

    @Override
    public String getFilename() {
        return getSdfFilename();
    }
}
