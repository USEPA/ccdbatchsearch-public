package gov.epa.ccte.api.batchsearch.service.filegenerators.excel;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.InChIKeySkeletonSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class InChIKeySkeletonToExcel extends ExcelBase implements ExportType {

    private InChIKeySkeletonSearch inChIKeySkeletonSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        List<String> downloadItems = Arrays.asList(searchForm.getDownloadItems());

        HashMap<String, List<SearchWithChemicalDetails>> results = inChIKeySkeletonSearch.getResults(searchForm);

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = inChIKeySkeletonSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        //safety data
        if(downloadItems.contains("SAFETY_DATA")){
            inChIKeySkeletonSearch.getDhsdata();        }

        // Related Substance sheet
        if(downloadItems.contains("RELATED_RELATIONSHIP")){
            relatedSubLookup = buildRelatedSublookup(inChIKeySkeletonSearch.getRelatedSubstance());
        }else{
            this.relatedSubLookup = null;
        }

        // Synonyms sheet
        if(downloadItems.contains("SYNONYM_IDENTIFIER")){
            chemicalSynonyms = buildChemicalSynonymslookup(inChIKeySkeletonSearch.getChemicalSynonyms());
        }else{
            this.chemicalSynonyms = null;
        }

        // Toxval batch search sheet
        if(downloadItems.contains("TOXVAL_DETAILS")){
            toxvalBatchSearch = buildToxvalBatchSearchlookup(inChIKeySkeletonSearch.getToxvalBatchSearch());
        }else{
            this.toxvalBatchSearch = null;
        }

        // Toxcast sheet
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            buildToxcastLookup(results, inChIKeySkeletonSearch.getAssayResults(), inChIKeySkeletonSearch.getAssaysList());
        }else{
            this.assayColumns = null;
            this.toxcastLookup = null;
        }

        List<String> searchWords = List.of(searchForm.getSearchItems().split("\n"));

        addHeader(List.of("INPUT", "FOUND_BY", "DTXSID", "PREFERRED_NAME"), downloadItems, searchWords, results);

        int foundCount = 0;
        int notFoundCount = 0;
        int duplicates = 0;

        for (String word : searchWords) {
            String inchikey = word.split("-")[0];
            if (results.containsKey(inchikey)) {
                foundCount++;
                List<SearchWithChemicalDetails> detailsList = results.get(inchikey);
                for (SearchWithChemicalDetails details : detailsList) {
                    //csv.writeRow(parseDataForRow(details, searchForm.getDownloadItems()));

                    if(details.getSearchMatch().indexOf("WARNING") > 0)
                        duplicates++;

                    addDataRow(word, details);
                }
            } else {
                notFoundCount++;
                //csv.writeRow(parseDataForErrorRow(word, searchForm.getDownloadItems().length));
                addNotFoundRow(word);
            }
        }

        // Chemical Properties sheet
        if(downloadItems.contains("CHEMICAL_PROPERTIES_DETAILS")){
            updateChemicalPropertiesSheet(inChIKeySkeletonSearch.getChemicalProperties());
        }

        // update sheet for ASSOCIATED_TOXCAST_ASSAYS
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            updateToxcastSheet(inChIKeySkeletonSearch.getAssaysList());
        }
        
        // Update the Cover sheet
        updateCoverSheet(searchForm, foundCount, notFoundCount, duplicates);

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.inChIKeySkeletonSearch = (InChIKeySkeletonSearch) searchType;
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
