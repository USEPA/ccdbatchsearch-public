package gov.epa.ccte.api.batchsearch.domain.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import gov.epa.ccte.api.batchsearch.domain.ChemicalDetailsLong;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Immutable
//@Table(name = "vw_chemical_search_long", schema = "ccd_app")
@JsonIgnoreProperties({"searchMatch", "searchGroup", "searchWord", "modifiedValue", "rank"})
public class SearchWithChemicalDetails extends ChemicalDetailsLong {
    /**
     *
     */
    @Column(name = "search_name")
    @JsonProperty(value = "searchMatch")
    private String searchMatch;

    /**
     *
     */
    @Column(name = "search_group")
    @JsonProperty("searchGroup")
    private String searchGroup;

    /**
     *
     */
    @Column(name = "search_value")
    @JsonProperty("searchWord")
    private String searchWord;

    /**
     *
     */
    @Column(name = "modified_value")
    @JsonProperty("modifiedValue")
    private String modifiedValue;

    /**
     *
     */
    @Column(name = "rank")
    @JsonProperty("rank")
    private Integer rank;

}
