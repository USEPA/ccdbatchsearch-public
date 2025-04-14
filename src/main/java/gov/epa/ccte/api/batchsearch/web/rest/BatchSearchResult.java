package gov.epa.ccte.api.batchsearch.web.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BatchSearchResult {
    private String input; // search word
    private String foundBy;
    private String dtxsid;
    private String dtxcid;
    private String casrn;
    private String preferredName;
}
