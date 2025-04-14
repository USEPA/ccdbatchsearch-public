package gov.epa.ccte.api.batchsearch.service.filegenerators.excel;

import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportType;
import gov.epa.ccte.api.batchsearch.service.searchtypes.MsReadyFormulaSearch;
import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class MsReadyFormulaToExcel extends ExcelBase implements ExportType {

    private MsReadyFormulaSearch msReadyFormulaSearch;
    private BatchSearchForm searchForm;

    @Override
    public byte[] export() {
        // reset buffers
        initialize();

        List<String> downloadItems = new LinkedList<>(Arrays.asList(searchForm.getDownloadItems()));

        HashMap<String, List<SearchWithChemicalDetails>> results = msReadyFormulaSearch.getResults(searchForm);

        HashMap<String, String> processedSearchWords = msReadyFormulaSearch.getProcessedSearchName();

        // Chemical lists
        if(searchForm.getChemicalLists() != null && searchForm.getChemicalLists().length > 0) {
            this.chemlistLookup = msReadyFormulaSearch.getChemicalList(searchForm.getChemicalLists());
            this.selectedChemicalLists = searchForm.getChemicalLists();
        }else{
            this.chemlistLookup = null;
            this.selectedChemicalLists = null;
        }

        //safety data
        if(downloadItems.contains("SAFETY_DATA")){
            msReadyFormulaSearch.getDhsdata();        }

        // Related Substance sheet
        if(downloadItems.contains("RELATED_RELATIONSHIP")){
            relatedSubLookup = buildRelatedSublookup(msReadyFormulaSearch.getRelatedSubstance());
        }else{
            this.relatedSubLookup = null;
        }

        // Synonyms sheet
        if(downloadItems.contains("SYNONYM_IDENTIFIER")){
            chemicalSynonyms = buildChemicalSynonymslookup(msReadyFormulaSearch.getChemicalSynonyms());
        }else{
            this.chemicalSynonyms = null;
        }

        // Toxval batch search sheet
        if(downloadItems.contains("TOXVAL_DETAILS")){
            toxvalBatchSearch = buildToxvalBatchSearchlookup(msReadyFormulaSearch.getToxvalBatchSearch());
        }else{
            this.toxvalBatchSearch = null;
        }

        // Toxcast sheet
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            buildToxcastLookup(results, msReadyFormulaSearch.getAssayResults(), msReadyFormulaSearch.getAssaysList());
        }else{
            this.assayColumns = null;
            this.toxcastLookup = null;
        }

        log.debug("result = {} ", results.size());

        // MetFrag Input File - this option is only available with ms ready search
        if(downloadItems.contains("METFRAG_INPUT_FILE")) {
            downloadItems.remove("METFRAG_INPUT_FILE"); // removing it so that it won't showed up in excel file

            addHeader(List.of("INPUT", "FOUND_BY", "DTXCID_INDIVIDUAL_COMPONENT", "FORMULA_INDIVIDUAL_COMPONENT",
                    "SMILES_INDIVIDUAL_COMPONENT", "MAPPED_DTXSID", "PREFERRED_NAME_DTXSID","CASRN_DTXSID",
                    "FORMULA_MAPPED_DTXSID","SMILES_MAPPED_DTXSID","MS_READY_SMILES","INCHI_STRING_DTXCID",
                    "INCHIKEY_DTXCID","MONOISOTOPIC_MASS_DTXCID"), downloadItems, List.of(searchForm.getSearchItems().split("\n")), results);
        }else {
            addHeader(List.of("INPUT", "FOUND_BY", "DTXCID_INDIVIDUAL_COMPONENT",
                    "FORMULA_INDIVIDUAL_COMPONENT", "SMILES_INDIVIDUAL_COMPONENT",
                    "DTXSID", "PREFERRED_NAME"), downloadItems, List.of(searchForm.getSearchItems().split("\n")), results);
        }

        List<String> searchWords = List.of(searchForm.getSearchItems().split("\n"));


        int foundCount = 0;
        int notFoundCount = 0;
        int duplicates = 0;

        // rest of contents
        for (String searchWord : searchWords) {
            String key = processedSearchWords.get(searchWord);
            if (results.containsKey(key)) {
                foundCount++;
                List<SearchWithChemicalDetails> detailsList = results.get(key);
                for (SearchWithChemicalDetails detail : detailsList) {

                    if(detail.getSearchMatch().indexOf("WARNING") > 0)
                        duplicates++;

                    addDataRow(searchWord, detail);
                }
            } else {
                notFoundCount++;
                //csv.writeRow(parseDataForErrorRow(searchWord, searchForm.getDownloadItems().length));
                addNotFoundRowForMsReady(searchWord);
            }
        }

        // Chemical Properties sheet
        if(downloadItems.contains("CHEMICAL_PROPERTIES_DETAILS")){
            updateChemicalPropertiesSheet(msReadyFormulaSearch.getChemicalProperties());
        }

        // update sheet for ASSOCIATED_TOXCAST_ASSAYS
        if(downloadItems.contains("ASSOCIATED_TOXCAST_ASSAYS")){
            updateToxcastSheet(msReadyFormulaSearch.getAssaysList());
        }
        
        // Update the Cover sheet
        updateCoverSheet(searchForm, foundCount, notFoundCount, duplicates);

        return getContents();
    }

    @Override
    public void configure(SearchType searchType, BatchSearchForm searchForm) {
        this.msReadyFormulaSearch = (MsReadyFormulaSearch) searchType;
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
