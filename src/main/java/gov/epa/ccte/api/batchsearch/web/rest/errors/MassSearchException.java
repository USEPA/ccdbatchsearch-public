package gov.epa.ccte.api.batchsearch.web.rest.errors;

public class MassSearchException extends RuntimeException {

    public MassSearchException(Double start, Double end) {
        super("Search mass got error with " + start + " and " + end);
    }
}
