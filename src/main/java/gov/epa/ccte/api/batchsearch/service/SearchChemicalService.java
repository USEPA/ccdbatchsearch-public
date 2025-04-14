package gov.epa.ccte.api.batchsearch.service;

import gov.epa.ccte.api.batchsearch.domain.ChemicalListsAndDtxsids;
import gov.epa.ccte.api.batchsearch.domain.GhsLinkResponse;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Service
public class SearchChemicalService {

    private static String publicApiUrl;

    private static String ghsUrl;

    private static String apiKey;

    @Value("${application.api-url}")
    public void setPublicApiUrl( String publicApiUrl){
        SearchChemicalService.publicApiUrl = publicApiUrl;
    }

    @Value("${application.api-ghsurl}")
    public void setGhsUrl(String ghsUrl) {
        SearchChemicalService.ghsUrl = ghsUrl;
    }


    @Value("${application.apikey}")
    public void setApiKey( String apiKey){
        SearchChemicalService.apiKey = apiKey;
    }

    static Map<String, GhsLinkResponse> ghsLinkResponseMap = new HashMap<>();

    private final DecimalFormat casrnFormat = new DecimalFormat("#######-##-#");

    public List<String> getErrorMsg(String notFoundWord) {
        List<String> errors = new ArrayList<String>();

        if (isCasrn(notFoundWord)) {
            errors.add("Searched by CASRN: Found 0 results for '" + notFoundWord + "'.");
            if (checkCasrnFormat(notFoundWord, true) == false)
                errors.add("CAS number fails checksum.");
        } else if (isDtxcid(notFoundWord)) {
            errors.add("Searched by DTX Compound Id: Found 0 results for '" + notFoundWord + "'.");
        } else if (isDtxsid(notFoundWord)) {
            errors.add("Searched by DTX Substance Id: Found 0 results for '" + notFoundWord + "'.");
        } else if (isInchiKey(notFoundWord)) {
            errors.add("Searched by Inchi Key: Found 0 results for '" + notFoundWord + "'.");
        } else if (isInchiKeySkeleton(notFoundWord)) {
            errors.add("Searched by Inchi Key: Found 0 results for '" + notFoundWord + "'.");
        } else {
            errors.add("Searched by Synonym: Found 0 results for '" + notFoundWord + "'.");
        }

        return errors;
    }

    public String processNonCasrnSearchWord(String searchWord) {

        // From https://confluence.epa.gov/display/CCTEA/Search+Requirements
        // Make all character upper case
        searchWord = searchWord.toUpperCase();
        // Search word should be trim
        searchWord = searchWord.trim();

        searchWord = searchWord.replaceAll("-", " ");
        return searchWord;
    }

    public boolean isDtxcid(String dtxcid) {
        dtxcid = dtxcid.toUpperCase();
        return dtxcid.matches("DTXCID(.*)");
    }

    public boolean isDtxsid(String dtxsid) {
        dtxsid = dtxsid.toUpperCase();
        return dtxsid.matches("DTXSID(.*)");
    }

    public String toCasrn(String number) {
        return String.format("%s-%s-%s", number.substring(0, number.length() - 3), number.substring(number.length() - 3, number.length() - 1), number.substring(number.length() - 1));
    }

    public boolean isCasrn(String casrn) {
        return casrn.matches("^\\d{1,7}-\\d{2}-\\d$");
    }


    public boolean isInchiKey(String inchikey) {
        inchikey = inchikey.toUpperCase();
        return inchikey.matches("[A-Z]{14}-[A-Z]{10}-[A-Z]");
    }

    public boolean isInchiKeySkeleton(String inchikeyskeleton) {
        inchikeyskeleton = inchikeyskeleton.toUpperCase();
        return inchikeyskeleton.matches("[A-Z]{14}");
    }


    public boolean checkCasrnFormat(String casrn, boolean checkForDash) {
// Check the string against the mask
        if (checkForDash && !casrn.matches("^\\d{1,7}-\\d{2}-\\d$")) {
            return false;
        } else {
// Remove the dashes
            casrn = casrn.replaceAll("-", "");
            int sum = 0;
            for (int indx = 0; indx < casrn.length() - 1; indx++) {
                sum += (casrn.length() - indx - 1) * Integer.parseInt(casrn.substring(indx, indx + 1));
            }
// Check digit is the last char, compare to sum mod 10.
            log.debug("v1= {} and v2= {}", Integer.parseInt(casrn.substring(casrn.length() - 1)), (sum % 10));
            return Integer.parseInt(casrn.substring(casrn.length() - 1)) == (sum % 10);
        }
    }

    // This will remove duplicates(same dtxsid number) from search result
    public List<SearchWithChemicalDetails> removeDuplicatesForLong(List<SearchWithChemicalDetails> chemicals) {

        List<SearchWithChemicalDetails> returnList = new ArrayList<SearchWithChemicalDetails>();
        List<String> dtxsidList = new ArrayList<String>();
        List<String> dtxcidList = new ArrayList<String>();

        for (SearchWithChemicalDetails chemical : chemicals) {
            if (chemical.getDtxsid() != null) {
                if (dtxsidList.contains(chemical.getDtxsid()) == false) {
                    dtxsidList.add(chemical.getDtxsid());
                    returnList.add(chemical);
                } else {
                    log.debug("skip duplicate -  " + chemical);
                }
            }
        }
        return returnList;
    }

    public HashMap<String, String> preprocessingBatchSearch(List<String> inputTypes, List<String> searchWords) {

        HashMap<String, String> processedWords = new HashMap<String, String>();

        for (String searchWord : searchWords) {
            if (inputTypes.contains("inchikey")) {
                if (isInchiKey(searchWord)) {
                    processedWords.put(searchWord, searchWord);
                }
            } else if (inputTypes.contains("casrn")) {
                String temp = searchWord.replace(";", "").replaceAll("-", "");

                if (temp.contains("O")) {
                    // 75-O5-8 Letter O instead of 0
                    String temp2 = searchWord.replaceAll("-", "").replaceAll("O", "0");

                    if (isNumeric(temp2)) {
                        temp = temp2;
                    }
                } else if (temp.contains("/")) {
                    // 3/1/4860 Date Format: Should be 4860-03-1
                    String temp3 = searchWord.replaceAll("/", "");
                    if (isNumeric(temp3)) {
                        String[] splitCasrn = searchWord.split("/");
                        String temp4 = splitCasrn[2] + String.format("%02d", Integer.parseInt(splitCasrn[0])) + splitCasrn[1];
                        temp = temp4;
                    }
                }

                // casrn without dashes
                if (isNumeric(temp)) {
                    // remove leading zeros
                    String casrn = toCasrn(Integer.valueOf(temp).toString());
                    log.debug("{} convert into casrn = {}", searchWord, casrn);
                    processedWords.put(searchWord, casrn);
                } else
                    processedWords.put(searchWord, processNonCasrnSearchWord(searchWord));
            } else {
                processedWords.put(searchWord, processNonCasrnSearchWord(searchWord));
            }
        }

        return processedWords;
    }

    //  this will not check -ve and decimal numbers
    private Boolean isNumeric(String number) {
        for (char c : number.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;

//        return  number.chars().allMatch( Character::isDigit );
    }

    public String getDataNotFoundMsg(String notFoundWord, List<String> inputTypes) {

        String msg;

        if (isCasrn(notFoundWord)) {
            if (checkCasrnFormat(notFoundWord, true) == false)
                msg = "CAS number fails checksum.";
            else
                msg = "Searched by CASRN: Found 0 results";
        } else if (isDtxcid(notFoundWord)) {
            msg = "Searched by DTX Compound Id: Found 0 results";
        } else if (isDtxsid(notFoundWord)) {
            msg = "Searched by DTX Substance Id: Found 0 results ";
        } else if (isInchiKey(notFoundWord)) {
            msg = "Searched by Inchi Key: Found 0 results";
        } else if (isInchiKeySkeleton(notFoundWord)) {
            msg = "Searched by Inchi Key: Found 0 results ";
        } else {
            msg = "Searched by Synonym: Found 0 results";
        }
        return msg;
    }

    public HashMap<String, List<SearchWithChemicalDetails>> getResultHashMap(List<SearchWithChemicalDetails> identifierResult) {

        HashMap<String, List<SearchWithChemicalDetails>> results = new HashMap<>();

        String searchWord = "";
        List<SearchWithChemicalDetails> duplicates = new ArrayList<>();

        for (SearchWithChemicalDetails details : identifierResult) {
            if (searchWord.equals("")) {
                duplicates.add(details);
                searchWord = details.getModifiedValue();
            } else if (searchWord.equalsIgnoreCase(details.getModifiedValue())) {
                duplicates.add(details);
            } else {
                results.put(searchWord, duplicates);
                duplicates = new ArrayList<>();
                duplicates.add(details);
                searchWord = details.getModifiedValue();
            }
        }
        // last record
        results.put(searchWord, duplicates);

        return results;
    }

    //
    /*public HashMap<String, List<SearchInChemicalDetails>> getInChIKeyHashMap(List<SearchInChemicalDetails> identifierResult) {

        HashMap<String, List<SearchInChemicalDetails>> results = new HashMap<>();

        String inchikey = "";
        List<SearchInChemicalDetails> duplicates = new ArrayList<>();
        int i = 0;
        for(SearchInChemicalDetails details: identifierResult){
            String in =  details.getInchikey();
            String temp = details.getInchikey().split("-")[0];
            i++;
            if(inchikey.equals("")){
                duplicates.add(details);
                inchikey = temp;
            }else if(inchikey.equals(temp)){
                duplicates.add(details);
            }else{
                results.put(inchikey, duplicates);
                duplicates = new ArrayList<>();
                duplicates.add(details);
                inchikey = temp;
            }
        }
        // last record
        results.put(inchikey, duplicates);

        return results;
    }*/

   /* public HashMap<String, List<SearchInChemicalDetails>> getFormulaHashMap(List<SearchInChemicalDetails> formualMatchResult) {
        HashMap<String, List<SearchInChemicalDetails>> results = new HashMap<>();

        String formula = "";
        List<SearchInChemicalDetails> duplicates = new ArrayList<>();
        int i = 0;
        for(SearchInChemicalDetails details: formualMatchResult){
            String temp =  details.getMolFormula();
            i++;
            if(formula.equals("")){
                duplicates.add(details);
                formula = temp;
            }else if(formula.equals(temp)){
                duplicates.add(details);
            }else{
                results.put(formula, duplicates);
                duplicates = new ArrayList<>();
                duplicates.add(details);
                formula = temp;
            }
        }
        // last record
        results.put(formula, duplicates);

        return results;
    }*/

    public static Set<String> getChemicalList(String[] chemicalLists, Set<String> dtxsids) {


        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        // Create the request body
        ChemicalListsAndDtxsids requestBody = new ChemicalListsAndDtxsids();
        List<String> dtxsidList = new ArrayList<>(dtxsids);
        List<String> chemicalList = new ArrayList<>(List.of(chemicalLists));

        requestBody.setDtxsids(dtxsidList);
        requestBody.setChemicalLists(chemicalList);

        // Creating HttpEntity containing the headers and the request body
        HttpEntity<ChemicalListsAndDtxsids> entity = new HttpEntity<>(requestBody, headers);


        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(publicApiUrl);

        try {
            ResponseEntity<List<String>> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {
                    });
            List<String> result = response.getBody();
            return new HashSet<>(Objects.requireNonNull(result));
        } catch (Exception e) {
            e.printStackTrace();

        }
        return new HashSet<>();
    }


    public static Map<String, GhsLinkResponse> getGhsData(String[] dtxsids) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<String[]> entity = new HttpEntity<>(dtxsids, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ghsUrl);

        try {
            ResponseEntity<List<GhsLinkResponse>> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<List<GhsLinkResponse>>() {
                    });

            List<GhsLinkResponse> ghsLinkResponses = response.getBody();

            // Populate the map
            assert ghsLinkResponses != null;
            for (GhsLinkResponse ghsLinkResponse : ghsLinkResponses) {
                ghsLinkResponseMap.put(ghsLinkResponse.getDtxsid(), ghsLinkResponse);
            }

            return ghsLinkResponseMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static  Boolean getIsSafetyDataValue(String dtxsid){
        if (ghsLinkResponseMap.containsKey(dtxsid)){
            GhsLinkResponse response = ghsLinkResponseMap.get(dtxsid);
            if (response.getIsSafetyData() && response.getSafetyUrl()!= null) {
                return true;
            }
        }
        return false;
    }

    public static  String getSafetyUrl(String dtxsid){

        if (ghsLinkResponseMap.containsKey(dtxsid)){
            GhsLinkResponse response = ghsLinkResponseMap.get(dtxsid);
            if (response != null && response.getSafetyUrl() != null){
                return response.getSafetyUrl();
            }
        }
        return null;
    }


}