package gov.epa.ccte.api.batchsearch.service.searchtypes;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class SearchTypeFactory {

    static Map<String, SearchType> searchTypeMap = new HashMap<>();

    static {
        searchTypeMap.put("IDENTIFIER", new IdentifierSearch());
        searchTypeMap.put("INCHIKEY_SKELETON", new InChIKeySkeletonSearch());
        searchTypeMap.put("MASS", new MassSearch());
        searchTypeMap.put("DTXCID", new DtxcidSearch());
        searchTypeMap.put("EXACT_FORMULA", new ExactFormulaSearch());
        searchTypeMap.put("MSREADY_FORMULA", new MsReadyFormulaSearch());
    }

//    public static Optional<SearchType> getSearchType(String searchType) {
//        log.debug("search type={}", searchType);
//        SearchType searchType1 = searchTypeMap.get(searchType);
//        searchType1.initialize();
//        return Optional.ofNullable(searchType1);
//    }

    public static Optional<SearchType> getSearchType(String searchType) {
        log.debug("search type={}", searchType);

        switch (searchType){
            case "IDENTIFIER":
                return Optional.ofNullable(new IdentifierSearch());
            case "INCHIKEY_SKELETON":
                return Optional.ofNullable(new InChIKeySkeletonSearch());
            case "MASS":
                return Optional.ofNullable(new MassSearch());
            case "DTXCID":
                return Optional.ofNullable(new DtxcidSearch());
            case "EXACT_FORMULA":
                return Optional.ofNullable(new ExactFormulaSearch());
            case "MSREADY_FORMULA":
                return Optional.ofNullable(new MsReadyFormulaSearch());
            default:
                return null;
        }
    }
}
