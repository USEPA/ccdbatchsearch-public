package gov.epa.ccte.api.batchsearch.service.filegenerators.excel;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.MassSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class MassToExcel extends ExcelBase implements ExportType {

    private MassSearch massSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        List<String> downloadItems = Arrays.asList(searchForm.getDownloadItems());

        HashMap<String, List<SearchWithChemicalDetails>> results = massSearch.getResults(searchForm);

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = massSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        //safety data
        if(downloadItems.contains("SAFETY_DATA")){
            massSearch.getDhsdata();        }

        // Related Substance sheet
        if(downloadItems.contains("RELATED_RELATIONSHIP")){
            relatedSubLookup = buildRelatedSublookup(massSearch.getRelatedSubstance());
        }else{
            this.relatedSubLookup = null;
        }

        // Synonyms sheet
        if(downloadItems.contains("SYNONYM_IDENTIFIER")){
            chemicalSynonyms = buildChemicalSynonymslookup(massSearch.getChemicalSynonyms());
        }else{
            this.chemicalSynonyms = null;
        }

        // Toxval batch search sheet
        if(downloadItems.contains("TOXVAL_DETAILS")){
            toxvalBatchSearch = buildToxvalBatchSearchlookup(massSearch.getToxvalBatchSearch());
        }else{
            this.toxvalBatchSearch = null;
        }

        // Toxcast sheet
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            buildToxcastLookup(results, massSearch.getAssayResults(), massSearch.getAssaysList());
        }else{
            this.assayColumns = null;
            this.toxcastLookup = null;
        }

        log.debug("result = {} ", results.size());

        addHeader(List.of("INPUT", "MASS_DIFFERENCE", "FOUND_BY", "DTXCID_INDIVIDUAL_COMPONENT",
                "MONOISOTOPIC_MASS_INDIVIDUAL_COMPONENT", "SMILES_INDIVIDUAL_COMPONENT",
                "DTXSID", "PREFERRED_NAME"), downloadItems, List.of(searchForm.getSearchItems().split("\n")), results);

        int foundCount = 0;
        int notFoundCount = 0;
        int duplicates = 0;

        // rest of contents
        for (String searchWord : results.keySet()) {
            List<SearchWithChemicalDetails> details = results.get(searchWord);
            if (details != null && details.size() > 0) {
                foundCount++;
                for (SearchWithChemicalDetails detail : details) {
                    if (detail.getSearchMatch().indexOf("WARNING") > 0)
                        duplicates++;

                    addDataRow(searchWord, detail);
                }
            } else {
                notFoundCount++;
                // csv.writeRow(parseDataForErrorRow(searchWord, searchForm.getDownloadItems().length));
                addNotFoundRow(searchWord);
            }
        }

        // Chemical Properties sheet
        if(downloadItems.contains("CHEMICAL_PROPERTIES_DETAILS")){
            updateChemicalPropertiesSheet(massSearch.getChemicalProperties());
        }

        // update sheet for ASSOCIATED_TOXCAST_ASSAYS
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            updateToxcastSheet(massSearch.getAssaysList());
        }
        
        // Update the Cover sheet
        updateCoverSheet(searchForm, foundCount, notFoundCount, duplicates);

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.massSearch = (MassSearch) searchType;
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
