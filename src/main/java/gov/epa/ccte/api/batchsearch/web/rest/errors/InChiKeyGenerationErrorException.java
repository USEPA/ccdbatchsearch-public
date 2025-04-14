package gov.epa.ccte.api.batchsearch.web.rest.errors;

public class InChiKeyGenerationErrorException extends RuntimeException {

    public InChiKeyGenerationErrorException() {
        super("InChIKey not computable for provided structure");
    }
}
