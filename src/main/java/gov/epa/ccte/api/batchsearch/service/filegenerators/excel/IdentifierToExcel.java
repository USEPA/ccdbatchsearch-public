package gov.epa.ccte.api.batchsearch.service.filegenerators.excel;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.IdentifierSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IdentifierToExcel extends ExcelBase implements ExportType {

    private IdentifierSearch identifierSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        List<String> downloadItems = Arrays.asList(searchForm.getDownloadItems());

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

        // Related Substance sheet
        if(downloadItems.contains("RELATED_RELATIONSHIP")){
            relatedSubLookup = buildRelatedSublookup(identifierSearch.getRelatedSubstance());
        }else{
            this.relatedSubLookup = null;
        }


        // Synonyms sheet
        if(downloadItems.contains("SYNONYM_IDENTIFIER")){
            chemicalSynonyms = buildChemicalSynonymslookup(identifierSearch.getChemicalSynonyms());
        }else{
            this.chemicalSynonyms = null;
        }

        // Toxval batch search sheet
        if(downloadItems.contains("TOXVAL_DETAILS")){
            toxvalBatchSearch = buildToxvalBatchSearchlookup(identifierSearch.getToxvalBatchSearch());
        }else{
            this.toxvalBatchSearch = null;
        }

        // Toxval batch search sheet
        if(downloadItems.contains("TOXREF_DETAILS")){
            toxrefBatchSearch = buildToxrefBatchSearchlookup(identifierSearch.getToxrefBatchSearch());
        }else{
            this.toxrefBatchSearch = null;
        }

        // Toxcast sheet
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            buildToxcastLookup(results, identifierSearch.getAssayResults(), identifierSearch.getAssaysList());
        }else{
            this.assayColumns = null;
            this.toxcastLookup = null;
        }

        if(downloadItems.contains("SAFETY_DATA")){
          identifierSearch.getDhsdata();        }

        List<String> searchWords = List.of(searchForm.getSearchItems().split("\n"));

        addHeader(List.of("INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"), downloadItems, searchWords, results);

        int foundCount = 0;
        int notFoundCount = 0;
        int duplicates = 0;

        // rest of contents
        for (String searchWord : searchWords) {
            String key = processedSearchWords.get(searchWord);
            if (results.containsKey(key)) {
                foundCount++;
                List<SearchWithChemicalDetails> detailsList = results.get(key);
                for (SearchWithChemicalDetails details : detailsList) {

                    if(details.getSearchMatch().indexOf("WARNING") > 0)
                        duplicates++;

                    addDataRow(searchWord, details);
                }
            } else {
                notFoundCount++;
                addNotFoundRow(searchWord);
            }
        }

        // Chemical Properties sheet
        if(downloadItems.contains("CHEMICAL_PROPERTIES_DETAILS")){
            updateChemicalPropertiesSheet(identifierSearch.getChemicalProperties());
        }

        // update sheet for ASSOCIATED_TOXCAST_ASSAYS
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            updateToxcastSheet(identifierSearch.getAssaysList());
        }
        
        // Update the Cover sheet
        updateCoverSheet(searchForm, foundCount, notFoundCount, duplicates);

        return getContents();

    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.identifierSearch = (IdentifierSearch) searchType;
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
