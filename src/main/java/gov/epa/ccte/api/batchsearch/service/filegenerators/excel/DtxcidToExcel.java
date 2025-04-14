package gov.epa.ccte.api.batchsearch.service.filegenerators.excel;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.DtxcidSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class DtxcidToExcel extends ExcelBase implements ExportType {

    private DtxcidSearch dtxcidSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        List<String> downloadItems = Arrays.asList(searchForm.getDownloadItems());


        log.trace("start - exporting dtxcid csv data ");
        HashMap<String, List<SearchWithChemicalDetails>> results = dtxcidSearch.getResults(searchForm);

        log.debug("result = {} ", results.size());

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = dtxcidSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        //safety data
        if(downloadItems.contains("SAFETY_DATA")){
            dtxcidSearch.getDhsdata();        }

        // Related Substance sheet
        if(downloadItems.contains("RELATED_RELATIONSHIP")){
            relatedSubLookup = buildRelatedSublookup(dtxcidSearch.getRelatedSubstance());
        }else{
            this.relatedSubLookup = null;
        }

        // Synonyms sheet
        if(downloadItems.contains("SYNONYM_IDENTIFIER")){
            chemicalSynonyms = buildChemicalSynonymslookup(dtxcidSearch.getChemicalSynonyms());
        }else{
            this.chemicalSynonyms = null;
        }

        // Toxval batch search sheet
        if(downloadItems.contains("TOXVAL_DETAILS")){
            toxvalBatchSearch = buildToxvalBatchSearchlookup(dtxcidSearch.getToxvalBatchSearch());
        }else{
            this.toxvalBatchSearch = null;
        }

        if(downloadItems.contains("TOXREF_DETAILS")){
            toxrefBatchSearch = buildToxrefBatchSearchlookup(dtxcidSearch.getToxrefBatchSearch());
        }else{
            this.toxvalBatchSearch = null;
        }

        // Toxcast sheet
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            buildToxcastLookup(results, dtxcidSearch.getAssayResults(), dtxcidSearch.getAssaysList());
        }else{
            this.assayColumns = null;
            this.toxcastLookup = null;
        }


        HashMap<String, String> processedSearchWords = dtxcidSearch.getProcessedSearchName();

        List<String> searchWords = List.of(searchForm.getSearchItems().split("\n"));

        addHeader(List.of("INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"), downloadItems, searchWords, results);

        int foundCount = 0;
        int notFoundCount = 0;
        int duplicates = 0;

        // rest of contents
        for (String searchWord : searchWords) {
            if (results.containsKey(searchWord)) {
                foundCount++;
                // Here we only have one record per key, so get the top record only
                SearchWithChemicalDetails details = results.get(searchWord).get(0);

                if(details.getSearchMatch().indexOf("WARNING") > 0)
                    duplicates++;
                //csv.writeRow(parseDataForRow(details, searchForm.getDownloadItems()));
                addDataRow(searchWord, details);
            } else {
                notFoundCount++;
                //csv.writeRow(parseDataForErrorRow(searchWord, searchForm.getDownloadItems().length));
                addNotFoundRow(searchWord);
            }
        }

        // Chemical Properties sheet
        if(downloadItems.contains("CHEMICAL_PROPERTIES_DETAILS")){
            updateChemicalPropertiesSheet(dtxcidSearch.getChemicalProperties());
        }

        // update sheet for ASSOCIATED_TOXCAST_ASSAYS
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            updateToxcastSheet(dtxcidSearch.getAssaysList());
        }

        // Update the Cover sheet
        updateCoverSheet(searchForm, foundCount, notFoundCount, duplicates);

        return getContents();

    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.dtxcidSearch = (DtxcidSearch) searchType;
        this.searchForm = searchForm;
    }


    @Override
    public MediaType getContentType() {
        return getExcelContentType();
    }

    @Override
    public String getFilename() {
        return getExcelFilename();
    }

}
