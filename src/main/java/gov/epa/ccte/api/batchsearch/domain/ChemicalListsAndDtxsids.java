package gov.epa.ccte.api.batchsearch.domain;

import lombok.Data;

import java.util.List;

@Data
public class ChemicalListsAndDtxsids {
    List<String> dtxsids;
    List<String> chemicalLists;
}
