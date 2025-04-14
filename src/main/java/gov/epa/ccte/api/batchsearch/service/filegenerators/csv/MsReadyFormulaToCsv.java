package gov.epa.ccte.api.batchsearch.service.filegenerators.csv;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.MsReadyFormulaSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class MsReadyFormulaToCsv extends CsvBase implements ExportType {

    private MsReadyFormulaSearch msReadyFormulaSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        HashMap<String, List<SearchWithChemicalDetails>> results = msReadyFormulaSearch.getResults(searchForm);

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = msReadyFormulaSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        log.debug("result = {} ", results.size());

        addHeader(new String[]{"INPUT", "FOUND_BY", "DTXCID_INDIVIDUAL_COMPONENT",
                "FORMULA_INDIVIDUAL_COMPONENT", "SMILES_INDIVIDUAL_COMPONENT",
                "DTXSID", "PREFERRED_NAME"}, searchForm.getDownloadItems(),selectedChemicalLists);

        // rest of contents
        for (String searchWord : results.keySet()) {
            List<SearchWithChemicalDetails> details = results.get(searchWord);
            if (details != null && details.size() > 0) {
                for (SearchWithChemicalDetails detail : details)
                    //csv.writeRow(parseExactFormulaDataForRow(searchWord, detail, searchForm.getDownloadItems()));
                    addDataRow(searchWord, detail,selectedChemicalLists);
            } else {
                //csv.writeRow(parseDataForErrorRow(searchWord, searchForm.getDownloadItems().length));
                addNotFoundRow(searchWord);
            }
        }

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.msReadyFormulaSearch = (MsReadyFormulaSearch) searchType;
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
