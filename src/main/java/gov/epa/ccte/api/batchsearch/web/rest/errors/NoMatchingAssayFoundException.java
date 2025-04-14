package gov.epa.ccte.api.batchsearch.web.rest.errors;

public class NoMatchingAssayFoundException extends RuntimeException {

    public NoMatchingAssayFoundException(String searchWord) {
        super("No matching assay or gene found for " + searchWord);
    }
}