package gov.epa.ccte.api.batchsearch.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@MappedSuperclass
public abstract class ChemicalDetailsLong implements Serializable {
    /**
     *
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     *
     */
    @Column(name = "dtxsid")
    @JsonProperty("dtxsid")
    private String dtxsid;

    // column for UI - it is not in table
    @Transient
    @JsonProperty("selected")
    private Boolean selected;

    /**
     *
     */
    @Column(name = "dtxcid", length = 1)
    private String dtxcid;

    /**
     *
     */
    @Column(name = "casrn")
    @JsonProperty("casrn")
    private String casrn;

    /**
     *
     */
    @Column(name = "compound_id")
    @JsonProperty("compoundId")
    private Integer compoundId;

    /**
     *
     */
    @Column(name = "generic_substance_id")
    @JsonProperty("genericSubstanceId")
    private Integer genericSubstanceId;

    /**
     *
     */
    @Column(name = "preferred_name")
    @JsonProperty("preferredName")
    private String preferredName;

    /**
     *
     */
    @Column(name = "active_assays")
    @JsonProperty("activeAssays")
    private Integer activeAssays;

    /**
     *
     */
    @Column(name = "mol_formula")
    @JsonProperty("molFormula")
    private String molFormula;

    /**
     *
     */
    @Column(name = "monoisotopic_mass")
    @JsonProperty("monoisotopicMass")
    private Double monoisotopicMass;

    @Column(name = "mol_wgt")
    @JsonProperty("molWeight")
    private Double molWeight;

    /**
     *
     */
    @Column(name = "percent_assays")
    @JsonProperty("percentAssays")
    private Integer percentAssays;


    /**
     *
     */
    @Column(name = "sources_count")
    @JsonProperty("sourcesCount")
    private Long sourcesCount;

    /**
     *
     */
    @Column(name = "qc_level")
    @JsonProperty("qcLevel")
    private Integer qcLevel;

    /**
     *
     */
    @Column(name = "qc_level_desc")
    @JsonProperty("qcLevelDesc")
    private String qcLevelDesc;

    /**
     *
     */
    @Column(name = "stereo", length = 1)
    @JsonProperty("stereo")
    private String stereo;

    /**
     *
     */
    @Column(name = "isotope")
    @JsonProperty("isotope")
    private Integer isotope;

    /**
     *
     */
    @Column(name = "multicomponent")
    @JsonProperty("multicomponent")
    private Integer multicomponent;

    /**
     *
     */
    @Column(name = "total_assays")
    @JsonProperty("totalAssays")
    private Integer totalAssays;

    /**
     *
     */
    @Column(name = "toxcast_select")
    @JsonProperty("toxcastSelect")
    private String toxcastSelect;

    /**
     *
     */
    @Column(name = "pubchem_cid")
    @JsonProperty("pubchemCid")
    private Integer pubchemCid;

    /**
     *
     */
    @Column(name = "mol_file")
    @JsonProperty("molFile")
    private String molFile;

    /**
     *
     */
    @Column(name = "mrv_file")
    @JsonProperty("mrvFile")
    private String mrvFile;

    /**
     *
     */
    @Column(name = "related_substance_count")
    @JsonProperty("relatedSubstanceCount")
    private Long relatedSubstanceCount;

    /**
     *
     */
    @Column(name = "related_structure_count")
    @JsonProperty("relatedStructureCount")
    private Long relatedStructureCount;

    /**
     *
     */
    @Column(name = "has_structure_image")
    @JsonProperty("hasStructureImage")
    private Integer hasStructureImage;

    /**
     *
     */
    @Column(name = "iupac_name", length = 5000)
    @JsonProperty("iupacName")
    private String iupacName;

    /**
     *
     */
    @Column(name = "smiles")
    @JsonProperty("smiles")
    private String smiles;

    /**
     *
     */
    @Column(name = "inchi_string")
    @JsonProperty("inchiString")
    private String inchiString;

    /**
     *
     */
    @Column(name = "average_mass")
    @JsonProperty("averageMass")
    private Double averageMass;

    /**
     *
     */
    @Column(name = "inchikey")
    @JsonProperty("inchikey")
    private String inchikey;

    /**
     *
     */
    @Column(name = "qc_notes", length = 4000)
    @JsonProperty("qcNotes")
    private String qcNotes;

    /**
     *
     */
    @Column(name = "ms_ready_smiles")
    @JsonProperty("msReadySmiles")
    private String msReadySmiles;

    /**
     *
     */
    @Column(name = "qsar_ready_smiles")
    @JsonProperty("qsarReadySmiles")
    private String qsarReadySmiles;

    /**
     *
     */
    @Column(name = "iris_link")
    @JsonProperty("irisLink")
    private String irisLink;

    /**
     *
     */
    @Column(name = "pprtv_link")
    @JsonProperty("pprtvLink")
    private String pprtvLink;

    /**
     *
     */
    @Column(name = "wikipedia_article")
    @JsonProperty("wikipediaArticle")
    private String wikipediaArticle;

    /**
     *
     */
    @Column(name = "pc_code")
    @JsonProperty("pcCode")
    private String pcCode;

    /**
     *
     */
    @Column(name = "synonyms")
    @JsonProperty("synonyms")
    private String synonyms;

    /**
     *
     */
    @Column(name = "expocat_median_prediction")
    @JsonProperty("expocatMedianPrediction")
    private String expocatMedianPrediction;

    /**
     *
     */
    @Column(name = "expocat")
    @JsonProperty("expocat")
    private String expocat;

    /**
     *
     */
    @Column(name = "nhanes")
    @JsonProperty("nhanes")
    private String nhanes;

    /**
     *
     */
    @Column(name = "toxval_data")
    @JsonProperty("toxvalData")
    private String toxvalData;

    /**
     *
     */
    @Column(name = "water_solubility_test")
    @JsonProperty("waterSolubilityTest")
    private BigDecimal waterSolubilityTest;

    /**
     *
     */
    @Column(name = "water_solubility_opera")
    @JsonProperty("waterSolubilityOpera")
    private BigDecimal waterSolubilityOpera;

    /**
     *
     */
    @Column(name = "viscosity_cp_cp_test_pred")
    @JsonProperty("viscosityCpCpTestPred")
    private BigDecimal viscosityCpCpTestPred;

    /**
     *
     */
    @Column(name = "vapor_pressure_mmhg_test_pred")
    @JsonProperty("vaporPressureMmhgTestPred")
    private BigDecimal vaporPressureMmhgTestPred;

    /**
     *
     */
    @Column(name = "vapor_pressure_mmhg_opera_pred")
    @JsonProperty("vaporPressureMmhgOperaPred")
    private BigDecimal vaporPressureMmhgOperaPred;

    /**
     *
     */
    @Column(name = "thermal_conductivity")
    @JsonProperty("thermalConductivity")
    private BigDecimal thermalConductivity;

    /**
     *
     */
    @Column(name = "tetrahymena_pyriformis")
    @JsonProperty("tetrahymenaPyriformis")
    private BigDecimal tetrahymenaPyriformis;

    /**
     *
     */
    @Column(name = "surface_tension")
    @JsonProperty("surfaceTension")
    private BigDecimal surfaceTension;

    /**
     *
     */
    @Column(name = "soil_adsorption_coefficient")
    @JsonProperty("soilAdsorptionCoefficient")
    private BigDecimal soilAdsorptionCoefficient;

    /**
     *
     */
    @Column(name = "oral_rat_ld50_mol")
    @JsonProperty("oralRatLd50Mol")
    private BigDecimal oralRatLd50Mol;

    /**
     *
     */
    @Column(name = "opera_km_days_opera_pred")
    @JsonProperty("operaKmDaysOperaPred")
    private BigDecimal operaKmDaysOperaPred;

    /**
     *
     */
    @Column(name = "octanol_water_partition")
    @JsonProperty("octanolWaterPartition")
    private BigDecimal octanolWaterPartition;

    /**
     *
     */
    @Column(name = "octanol_air_partition_coeff")
    @JsonProperty("octanolAirPartitionCoeff")
    private BigDecimal octanolAirPartitionCoeff;

    /**
     *
     */
    @Column(name = "melting_point_degc_test_pred")
    @JsonProperty("meltingPointDegcTestPred")
    private BigDecimal meltingPointDegcTestPred;

    /**
     *
     */
    @Column(name = "melting_point_degc_opera_pred")
    @JsonProperty("meltingPointDegcOperaPred")
    private BigDecimal meltingPointDegcOperaPred;

    /**
     *
     */
    @Column(name = "hr_fathead_minnow")
    @JsonProperty("hrFatheadMinnow")
    private BigDecimal hrFatheadMinnow;

    /**
     *
     */
    @Column(name = "hr_diphnia_lc50")
    @JsonProperty("hrDiphniaLc50")
    private BigDecimal hrDiphniaLc50;

    /**
     *
     */
    @Column(name = "henrys_law_atm")
    @JsonProperty("henrysLawAtm")
    private BigDecimal henrysLawAtm;

    /**
     *
     */
    @Column(name = "flash_point_degc_test_pred")
    @JsonProperty("flashPointDegcTestPred")
    private BigDecimal flashPointDegcTestPred;

    /**
     *
     */
    @Column(name = "devtox_test_pred")
    @JsonProperty("devtoxTestPred")
    private BigDecimal devtoxTestPred;

    /**
     *
     */
    @Column(name = "density")
    @JsonProperty("density")
    private BigDecimal density;

    /**
     *
     */
    @Column(name = "boiling_point_degc_test_pred")
    @JsonProperty("boilingPointDegcTestPred")
    private BigDecimal boilingPointDegcTestPred;

    /**
     *
     */
    @Column(name = "boiling_point_degc_opera_pred")
    @JsonProperty("")
    private BigDecimal boilingPointDegcOperaPred;

    /**
     *
     */
    @Column(name = "biodegradation_half_life_days")
    @JsonProperty("biodegradationHalfLifeDays")
    private BigDecimal biodegradationHalfLifeDays;

    /**
     *
     */
    @Column(name = "bioconcentration_factor_test_pred")
    @JsonProperty("bioconcentrationFactorTestPred")
    private BigDecimal bioconcentrationFactorTestPred;

    /**
     *
     */
    @Column(name = "bioconcentration_factor_opera_pred")
    @JsonProperty("bioconcentrationFactorOperaPred")
    private BigDecimal bioconcentrationFactorOperaPred;

    /**
     *
     */
    @Column(name = "atmospheric_hydroxylation_rate")
    @JsonProperty("atmosphericHydroxylationRate")
    private BigDecimal atmosphericHydroxylationRate;

    /**
     *
     */
    @Column(name = "ames_mutagenicity_test_pred")
    @JsonProperty("amesMutagenicityTestPred")
    private BigDecimal amesMutagenicityTestPred;

    /**
     *
     */
    @Column(name = "descriptor_string_tsv", length = 20000)
    @JsonProperty("descriptorStringTsv")
    private String descriptorStringTsv;

    /**
     *
     */
    @Column(name = "pkaa_opera_pred")
    @JsonProperty("pkaaOperaPred")
    private BigDecimal pkaaOperaPred ;

    /**
     *
     */
    @Column(name = "pkab_opera_pred ")
    @JsonProperty("pkabOperaPred")
    private BigDecimal pkabOperaPred;

    @Column(name = "logd5_5")
    @JsonProperty("logd55")
    private BigDecimal logD55;

    @Column(name = "logd7_4")
    @JsonProperty("logD74")
    private BigDecimal logD74;

    @Column(name = "ready_bio_deg")
    @JsonProperty("readyBioDeg")
    private BigDecimal readyBioDeg;
}
