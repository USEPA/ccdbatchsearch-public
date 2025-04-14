package gov.epa.ccte.api.batchsearch.service.filegenerators;

import gov.epa.ccte.api.batchsearch.service.searchtypes.SearchType;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import org.springframework.http.*;

public interface ExportType {

    byte[] export();

    void configure(SearchType searchType, BatchSearchForm searchForm);

    // methods for download file
    MediaType getContentType();

    String getFilename();

    default ResponseEntity<byte[]> getResponseEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getContentType());
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(getFilename())
                .build());
        return new ResponseEntity<>(export(), headers, HttpStatus.OK);
    }
}
