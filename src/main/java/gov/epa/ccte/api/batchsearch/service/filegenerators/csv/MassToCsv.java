package gov.epa.ccte.api.batchsearch.service.filegenerators.csv;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.MassSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class MassToCsv extends CsvBase implements ExportType {

    private MassSearch massSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        HashMap<String, List<SearchWithChemicalDetails>> results = massSearch.getResults(searchForm);

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = massSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        log.debug("result = {} ", results.size());

        addHeader(new String[]{"INPUT", "MASS_DIFFERENCE", "FOUND_BY", "DTXCID_INDIVIDUAL_COMPONENT",
                "MONOISOTOPIC_MASS_INDIVIDUAL_COMPONENT", "SMILES_INDIVIDUAL_COMPONENT",
                "DTXSID", "PREFERRED_NAME"}, searchForm.getDownloadItems(),selectedChemicalLists);

        // rest of contents
        for (String searchWord : results.keySet()) {
            List<SearchWithChemicalDetails> details = results.get(searchWord);
            log.debug("search word={} and result count={}", searchWord, details.size());

            if (details != null && details.size() > 0) {
                for (SearchWithChemicalDetails detail : details)
                    //    csv.writeRow(parseMassDataForRow(searchWord, parseStartMassValue(searchWord),detail, searchForm.getDownloadItems()));
                    addDataRow(searchWord, detail,selectedChemicalLists);
            } else {
                // csv.writeRow(parseDataForErrorRow(searchWord, searchForm.getDownloadItems().length));
                addNotFoundRow(searchWord);
            }
        }

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.massSearch = (MassSearch) searchType;
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
