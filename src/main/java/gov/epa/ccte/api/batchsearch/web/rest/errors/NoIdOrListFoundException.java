package gov.epa.ccte.api.batchsearch.web.rest.errors;

public class NoIdOrListFoundException extends RuntimeException {

    public NoIdOrListFoundException(String dtxsid, String listName) {
        super("No matching id=" + dtxsid + " or/and list=" + listName + " found. ");
    }
}
