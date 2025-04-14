package gov.epa.ccte.api.batchsearch.service.searchtypes;

import gov.epa.ccte.api.batchsearch.domain.*;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.repository.*;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;

import java.util.*;

public interface SearchType {

    void initialize();

    List<String> getAssaysList();

    List<BioactivityAssayList> getAssayResults();

    List<RelatedSubstance> getRelatedSubstance();

    List<ChemicalSynonym> getChemicalSynonyms();

    List<ToxvalBatchSearch> getToxvalBatchSearch();

    List<ToxrefBatchSearch> getToxrefBatchSearch();

    List<ChemicalProperties> getChemicalProperties();

    List<ChemlistToSubtance> getChemlistToSubstance();

    List<ChemicalDetails> getChemicals(BatchSearchForm formBean);

    Set<String> getChemicalList(String[] chemicalLists);

    HashMap<String, List<SearchWithChemicalDetails>> getResults(BatchSearchForm formBean);

    void setRepository(SearchWithChemicalDetailsRepository searchWithChemicalDetailsRepository,
                       ChemicalDetailsRepository detailsRepository,
                       ChemlistToSubtanceRepository chemlistToSubtanceRepository,
                       ChemicalSynonymRepository synonymRepository,
                       RelatedSubstanceRepository relatedSubstanceRepository,
                       BioactivityAssayListRepository assayListRepository,
                       ToxvalBatchSearchRepository toxvalBatchSearchRepository,
                       ToxrefBatchSearchRepository toxrefBatchSearchRepository,
                       ChemicalPropertiesRepository propertiesRepository);

    HashMap<String, String> getProcessedSearchName();

    void getDhsdata();

    static List<String> getSearchNames(List<String> inputTypes) {

        List<String> searchMatchToInclude = new ArrayList<>();
        List<String> searchMatchForChemicalName = Arrays.asList("Approved Name", "Synonym", "Systematic Name", "Integrated Source Name", "Expert Validated Synonym", "Synonym from Valid Source", "FDA CAS-Like Identifier", "EHCA Number");
        List<String> searchMatchForCasrn = Arrays.asList("Deleted CAS-RN", "Alternate CAS-RN", "CAS-RN", "Integrated Source CAS-RN");
        List<String> searchMatchForInchikey = Arrays.asList("InChIKey", "Indigo InChIKey");

        for (String inputtype : inputTypes) {

            if (inputtype.equalsIgnoreCase("chemical_name")) {
                searchMatchToInclude.addAll(searchMatchForChemicalName);
            } else if (inputtype.equalsIgnoreCase("casrn")) {
                searchMatchToInclude.addAll(searchMatchForCasrn);
            } else if (inputtype.equalsIgnoreCase("inchikey")) {
                searchMatchToInclude.addAll(searchMatchForInchikey);
            } else if (inputtype.equalsIgnoreCase("DSSTox_Substance_Id")) {
                searchMatchToInclude.add("DSSTox_Substance_Id");
            } else if (inputtype.equalsIgnoreCase("DSSTox_Compound_Id")) {
                searchMatchToInclude.add("DSSTox_Compound_Id");
            } else if (inputtype.equalsIgnoreCase("skeleton")) {
                searchMatchToInclude.addAll(searchMatchForInchikey);
            }
        }

        return searchMatchToInclude;
    }

    static String buildKey(String dtxsid, String dtxcid){
        return dtxsid + "-" + dtxcid;
    }

}
