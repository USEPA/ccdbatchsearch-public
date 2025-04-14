package gov.epa.ccte.api.batchsearch.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Immutable;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;


@Entity
@Data
@Table(name = "chemical_details")
public class ChemicalDetails implements Serializable {

    @Id
    @Column(name = "h_chem_hash_key")
    private String id;

    @Column(name = "dtxsid")
    @JsonProperty("dtxsid")
    private String dtxsid;

    // column for UI - it is not in table
    @Transient
    @JsonProperty(value = "selected", required = false, defaultValue = "false")
    private Boolean selected;

    @Column(name = "dtxcid")
    @JsonProperty("dtxcid")
    private String dtxcid;

    @Column(name = "generic_substance_id", nullable = true)
    @JsonProperty("genericSubstanceId")
    private Integer genericSubstanceId;

    @Column(name = "casrn")
    @JsonProperty("casrn")
    private String casrn;

    @Column(name = "preferred_name")
    @JsonProperty("preferredName")
    private String preferredName;

    @Column(name = "compound_id")
    @JsonProperty("compoundId")
    private Integer compoundId;

    @Column(name = "stereo")
    @JsonProperty("stereo")
    private Integer stereo;

    @Column(name = "isotope")
    @JsonProperty("isotope")
    private Integer isotope;

    @Column(name = "multicomponent")
    @JsonProperty("multicomponent")
    private Integer multicomponent;

    @Column(name = "sources_count")
    @JsonProperty("sourcesCount")
    private Integer sourcesCount;

    @Column(name = "active_assays")
    @JsonProperty("activeAssays")
    private Integer activeAssays;

    @Column(name = "total_assays")
    @JsonProperty("totalAssays")
    private Integer totalAssays;

    @Column(name = "percent_assays")
    @JsonProperty("percentAssays")
    private BigInteger percentAssays;

    @Column(name = "toxcast_select")
    @JsonProperty("toxcastSelect")
    private String toxcastSelect;

    @Column(name = "monoisotopic_mass")
    @JsonProperty("monoisotopicMass")
    private Double monoisotopicMass;

    @Column(name = "mol_formula")
    @JsonProperty("molFormula")
    private String molFormula;

    @Column(name = "qc_level")
    @JsonProperty("qcLevel")
    private Integer qcLevel;

    @Column(name = "qc_level_desc")
    @JsonProperty("qcLevelDesc")
    private String qcLevelDesc;

    @Column(name = "pubchem_cid")
    @JsonProperty("pubchemCid")
    private Integer pubchemCid;

    @Column(name = "has_structure_image")
    @JsonProperty("hasStructureImage")
    private Boolean hasStructureImage;

    @Column(name = "related_substance_count")
    @JsonProperty("relatedSubstanceCount")
    private Integer relatedSubstanceCount;

    @Column(name = "related_structure_count")
    @JsonProperty("relatedStructureCount")
    private Integer relatedStructureCount;

    @Column(name = "iupac_name")
    @JsonProperty("iupacName")
    private String iupacName;

    @Column(name = "smiles")
    @JsonProperty("smiles")
    private String smiles;

    @Column(name = "inchi_string")
    @JsonProperty("inchiString")
    private String inchiString;

    @Column(name = "inchikey")
    @JsonProperty("inchiKey")
    private String inchikey;

    @Column(name = "average_mass")
    @JsonProperty("averageMass")
    private Double averageMass;

}
