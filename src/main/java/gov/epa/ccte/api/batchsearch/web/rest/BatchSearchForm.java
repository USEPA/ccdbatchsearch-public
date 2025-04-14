package gov.epa.ccte.api.batchsearch.web.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class BatchSearchForm {
    private String inputType; // IDENTIFIER,INCHIKEY_SKELETON,DTXCID,MS_READY_FORMULA,EXACT_FORMULA, MASS
    private String[] identifierTypes;
    private String searchItems; // EOL separated
    private Double massError;
    private String[] downloadItems;
    private String downloadType; // excel, csv, sdf
    private String jobId;
    private String molVersion="V2000";
    private String[] chemicalLists;
}
