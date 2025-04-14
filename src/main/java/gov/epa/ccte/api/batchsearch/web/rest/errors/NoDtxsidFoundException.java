package gov.epa.ccte.api.batchsearch.web.rest.errors;

public class NoDtxsidFoundException extends RuntimeException {

    public NoDtxsidFoundException(String searchWord) {
        super("No matching dtxsid found for " + searchWord);
    }
}
