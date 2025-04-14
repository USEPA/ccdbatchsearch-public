package gov.epa.ccte.api.batchsearch.service.filegenerators.excel;

import gov.epa.ccte.api.batchsearch.domain.*;
import gov.epa.ccte.api.batchsearch.domain.search.SearchWithChemicalDetails;
import gov.epa.ccte.api.batchsearch.service.SearchChemicalService;
import gov.epa.ccte.api.batchsearch.service.Validators;
import gov.epa.ccte.api.batchsearch.service.filegenerators.ExportBase;
import gov.epa.ccte.api.batchsearch.web.rest.BatchSearchForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
abstract class ExcelBase extends ExportBase {

    // Toxcast assays sheet
    List<String> assayColumns;  // these are search words which has matching records
    protected Hashtable<String, List<String>> toxcastLookup;

    protected List<String> headerWithToxprint;

    // for chemical lists export with excel
    protected Set<String> chemlistLookup;
    protected String[] selectedChemicalLists;

    // Synonyms
    protected Hashtable<String, ChemicalSynonym> chemicalSynonyms;
    protected Hashtable<String, List<RelatedSubstance>> relatedSubLookup;

    //toxcast
    protected Hashtable<String,List<ToxvalBatchSearch>> toxvalBatchSearch;

    //toxref
    protected Hashtable<String,List<ToxrefBatchSearch>> toxrefBatchSearch;


    protected Hashtable<String, ChemicalProperties> chemicalProperties;


    //timestamp format
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int ROWS_IN_MEMORY = 100; // keep these number of rows in memory
    private static final List<String> ABSTRACT_SHIFTER_COLS = Arrays.asList(new String[]{"DSSTOX_LINK_TO_DASHBOARD", "PREFERRED_NAME", "CHEMICAL/ENTITY_QUERY"});
    public static final List<String> SYNONYM_IDENTIFIER_COLS = Arrays.asList(new String[]{"SEARCHED_CHEMICAL", "IDENTIFIER", "PC-CODES"});
    public static final List<String> RELATED_RELATIONSHIP_COLS = Arrays.asList(new String[]{"INPUT", "DTXSID", "PREFERRED_NAME", "HAS_RELATIONSHIP_WITH", "RELATED_DTXSID", "RELATED_PREFERRED_NAME", "RELATED_CASRN"});
    public static final List<String> ASSOCIATED_TOXCAST_ASSAYS_COLS = Arrays.asList(new String[]{"INPUT"}); // RESULT will be replace with actual result value
    public static final List<String> TOXVAL_IDENTIFIER_COLS = Arrays.asList(new String[]{"SEARCHED_CHEMICAL", "DTXSID", "CASRN", "NAME", "SOURCE", "SUB_SOURCE", "TOXVAL_TYPE", "TOXVAL_SUBTYPE", "TOXVAL_TYPE_SUPERCATEGORY", "QUALIFIER", "TOXVAL_NUMERIC", "TOXVAL_UNITS", "RISK_ASSESSMENT_CLASS", "STUDY_TYPE", "STUDY_DURATION_CLASS", "STUDY_DURATION_VALUE", "STUDY_DURATION_UNITS", "SPECIES_COMMON", "STRAIN", "LATIN_NAME", "SPECIES_SUPERCATEGORY", "SEX", "GENERATION", "LIFESTAGE", "EXPOSURE_ROUTE", "EXPOSURE_METHOD", "EXPOSURE_FORM", "MEDIA", "EFFECT", "EXPERIMENTAL_RECORD", "STUDY_GROUP", "LONG_REF", "DOI", "TITLE", "AUTHOR", "YEAR", "GUIDELINE", "QUALITY", "QC_CATEGORY", "SOURCE_HASH", "EXTERNAL_SOURCE_ID", "SOURCE_URL", "SUBSOURCE_URL", "STORED_SOURCE_RECORD", "TOXVAL_TYPE_ORIGINAL", "TOXVAL_SUBTYPE_ORIGINAL", "TOXVAL_NUMERIC_ORIGINAL", "TOXVAL_UNITS_ORIGINAL", "STUDY_TYPE_ORIGINAL", "STUDY_DURATION_CLASS_ORIGINAL", "STUDY_DURATION_VALUE_ORIGINAL", "STUDY_DURATION_UNITS_ORIGINAL", "SPECIES_ORIGINAL", "STRAIN_ORIGINAL", "SEX_ORIGINAL", "GENERATION_ORIGINAL", "LIFESTAGE_ORIGINAL", "EXPOSURE_ROUTE_ORIGINAL", "EXPOSURE_METHOD_ORIGINAL", "EXPOSURE_FORM_ORIGINAL", "MEDIA_ORIGINAL", "EFFECT_ORIGINAL",
            "ORIGINAL_YEAR"}); // RESULT will be replace with actual result value
    public static final List<String> CHEMICAL_PROPERTIES_DETAILS_COLS = Arrays.asList(new String[]{"DTXSID","DTXCID","TYPE","NAME","VALUE","UNITS","SOURCE","DESCRIPTION"});
    public static final List<String> TOXREF_IDENTIFIER_COLS = Arrays.asList(new String[]{"STUDY_ID", "PREFFERED_NAME","DSSTOX_SUBSTANCE_ID", "CASRN", "STUDY_SOURCE", "STUDY_SOURCE_ID", "CITATION", "STUDY_YEAR", "STUDY_TYPE", "STUDY_TYPE_GUIDELINE", "SPECIES", "STRAIN_GROUP", "STRAIN", "ADMIN_ROUTE", "ADMIN_METHOD", "DOSE_DURATION", "DOSE_DURATION_UNIT", "DOSE_START", "DOSE_START_UNIT", "DOSE_END", "DOSE_END_UNIT", "DOSE_PERIOD", "DOSE_LEVEL", "CONC", "CONC_UNIT", "VEHICLE", "DOSE_COMMENT", "DOSE_ADJUSTED", "DOSE_ADJUSTED_UNIT", "SEX", "GENERATION", "LIFE_STAGE", "NUM_ANIMALS", "TG_COMMENT", "ENDPOINT_CATEGORY", "ENDPOINT_TYPE", "ENDPOINT_TARGET", "EFFECT_DESC", "EFFECT_DESC_FREE", "CANCER_RELATED", "TARGET_SITE", "DIRECTION", "EFFECT_COMMENT", "TREATMENT_RELATED", "CRITICAL_EFFECT", "SAMPLE_SIZE", "EFFECT_VAL", "EFFECT_VAL_UNIT", "EFFECT_VAR", "EFFECT_VAR_TYPE", "TIME", "TIME_UNIT", "NO_QUANT_DATA_REPORTED"});
    // Sheet names
    protected final String COVER_SHEET = "Cover Sheet";
    protected final String MAIN_DATA_SHEET = "Main Data";
    protected final String ABSTRACT_SHIFTER = "Abstract Sifter";
    protected final String SYNONYM_IDENTIFIER = "Synonym Identifier";
    protected final String RELATED_RELATIONSHIP = "Related Relationships";
    protected final String ASSOCIATED_TOXCAST_ASSAYS = "ToxCast Assays AC50";
    protected final String TOXVAL_DETAILS = "Toxval Details";
    protected final String TOXREF_DETAILS = "Toxref Details";
    protected final String CHEMICAL_PROPERTIES_DETAILS = "Chemical Properties";


    // Hyperlink column
    private final List<String> hyperLinkColumns = new ArrayList<>(Arrays.asList("DTXSID","DTXCID","EXPOCAST","NHANES", "TOXVAL_DATA",
            "IRIS_LINK", "PPRTV_LINK", "WIKIPEDIA_ARTICLE","DSSTOX_LINK_TO_DASHBOARD","SAFETY_DATA"));

    private HashMap<String, List<String>> headers = new HashMap<>();
    private HashMap<String,Integer> sheetCurrentRow = new HashMap<>();
    protected SXSSFWorkbook workbook;
    protected SXSSFSheet sheet;

    // styles - excel (defining them as class level to reduce the number)
    CellStyle dataRowStyle;
    CellStyle numericDataStyle;
    CellStyle hyperLinkStyle;
    CellStyle headerStyle;

    //Toxprint fingerprint columns

    final String toxprintHeaderValues = "atom:element_main_group,atom:element_metal_group_I_II,atom:element_metal_group_III," +
            "atom:element_metal_metalloid,atom:element_metal_poor_metal,atom:element_metal_transistion_metal," +
            "atom:element_noble_gas,bond:C#N_cyano_acylcyanide,bond:C#N_cyano_cyanamide,bond:C#N_cyano_cyanohydrin," +
            "bond:C#N_nitrile_ab-acetylenic,bond:C#N_nitrile_ab-unsaturated,bond:C#N_nitrile_generic,bond:C#N_nitrile_isonitrile," +
            "bond:C#N_nitrile,bond:C(~Z)~C~Q_a-haloalcohol,bond:C(~Z)~C~Q_a-halocarbonyl,bond:C(~Z)~C~Q_a-haloether," +
            "bond:C(~Z)~C~Q_a-haloketone_perhalo,bond:C(~Z)~C~Q_b-halocarbonyl,bond:C(~Z)~C~Q_haloamine_haloethyl_(N-mustard)," +
            "bond:C(~Z)~C~Q_halocarbonyl_dichloro_quinone_(1_2-),bond:C(~Z)~C~Q_halocarbonyl_dichloro_quinone_(1_4-)," +
            "bond:C(~Z)~C~Q_halocarbonyl_dichloro_quinone_(1_5-),bond:C(~Z)~C~Q_haloether_dibenzodioxin_1-halo," +
            "bond:C(~Z)~C~Q_haloether_dibenzodioxin_2-halo,bond:C(~Z)~C~Q_haloether_dibenzodioxin_dichloro_(2_7-)," +
            "bond:C(~Z)~C~Q_haloether_dibenzodioxin_tetrachloro_(2_3_7_8-),bond:C(~Z)~C~Q_haloether_pyrimidine_2-halo-," +
            "bond:C(=O)N_carbamate_dithio,bond:C(=O)N_carbamate_thio,bond:C(=O)N_carbamate_thio_generic,bond:C(=O)N_carbamate," +
            "bond:C(=O)N_carboxamide_(NH2),bond:C(=O)N_carboxamide_(NHR),bond:C(=O)N_carboxamide_(NR2),bond:C(=O)N_carboxamide_generic," +
            "bond:C(=O)N_carboxamide_thio,bond:C(=O)N_dicarboxamide_N-hydroxy,bond:C(=O)O_acidAnhydride," +
            "bond:C(=O)O_carboxylicAcid_alkenyl,bond:C(=O)O_carboxylicAcid_alkyl,bond:C(=O)O_carboxylicAcid_aromatic," +
            "bond:C(=O)O_carboxylicAcid_generic,bond:C(=O)O_carboxylicEster_4-nitrophenol," +
            "bond:C(=O)O_carboxylicEster_acyclic,bond:C(=O)O_carboxylicEster_aliphatic," +
            "bond:C(=O)O_carboxylicEster_alkenyl,bond:C(=O)O_carboxylicEster_alkyl," +
            "bond:C(=O)O_carboxylicEster_aromatic,bond:C(=O)O_carboxylicEster_cyclic_b-propiolactone,bond:C(=O)O_carboxylicEster_N-hydroxytriazole_(aromatic)," +
            "bond:C(=O)O_carboxylicEster_O-pentafluorophenoxy,bond:C(=O)O_carboxylicEster_thio,bond:C=N_carbodiimide," +
            "bond:C=N_carboxamidine_generic,bond:C=N_guanidine_generic,bond:C=N_imine_C(connect_H_gt_0),bond:C=N_imine_N(connect_noZ)," +
            "bond:C=N_imine_oxy,bond:C=O_acyl_halide,bond:C=O_acyl_hydrazide,bond:C=O_aldehyde_alkyl,bond:C=O_aldehyde_aromatic," +
            "bond:C=O_aldehyde_generic,bond:C=O_carbonyl_1_2-di,bond:C=O_carbonyl_ab-acetylenic," +
            "bond:C=O_carbonyl_ab-unsaturated_aliphatic_(michael_acceptors),bond:C=O_carbonyl_ab-unsaturated_generic," +
            "bond:C=O_carbonyl_azido,bond:C=O_carbonyl_generic,bond:C=S_acyl_thio_halide,bond:C=S_carbonyl_thio_generic," +
            "bond:CC(=O)C_ketone_aliphatic_acyclic,bond:CC(=O)C_ketone_aliphatic_generic,bond:CC(=O)C_ketone_alkane_cyclic," +
            "bond:CC(=O)C_ketone_alkane_cyclic_(C4),bond:CC(=O)C_ketone_alkane_cyclic_(C5),bond:CC(=O)C_ketone_alkane_cyclic_(C6)," +
            "bond:CC(=O)C_ketone_alkene_cyclic_(C6),bond:CC(=O)C_ketone_alkene_cyclic_(C7)," +
            "bond:CC(=O)C_ketone_alkene_cyclic_2-en-1-one,bond:CC(=O)C_ketone_alkene_cyclic_2-en-1-one_generic," +
            "bond:CC(=O)C_ketone_alkene_cyclic_3-en-1-one,bond:CC(=O)C_ketone_alkene_generic,bond:CC(=O)C_ketone_aromatic_aliphatic,bond:CC(=O)C_ketone_generic," +
            "bond:CC(=O)C_ketone_methyl_aliphatic,bond:CC(=O)C_quinone_1_2-benzo,bond:CC(=O)C_quinone_1_2-naphtho," +
            "bond:CC(=O)C_quinone_1_4-benzo,bond:CC(=O)C_quinone_1_4-naphtho,bond:CN_amine_alicyclic_generic," +
            "bond:CN_amine_aliphatic_generic,bond:CN_amine_alkyl_ethanolamine,bond:CN_amine_alkyl_methanolamine,bond:CN_amine_aromatic_benzidine," +
            "bond:CN_amine_aromatic_generic,bond:CN_amine_aromatic_N-hydroxy,bond:CN_amine_pri-NH2_alkyl,bond:CN_amine_pri-NH2_aromatic," +
            "bond:CN_amine_pri-NH2_generic,bond:CN_amine_sec-NH_alkyl,bond:CN_amine_sec-NH_aromatic,bond:CN_amine_sec-NH_aromatic_aliphatic,bond:CN_amine_sec-NH_generic," +
            "bond:CN_amine_ter-N_aliphatic,bond:CN_amine_ter-N_aromatic,bond:CN_amine_ter-N_aromatic_aliphatic,bond:CN_amine_ter-N_generic," +
            "bond:CNO_amineOxide_aromatic,bond:CNO_amineOxide_dimethyl_alkyl,bond:CNO_amineOxide_generic,bond:COC_ether_aliphatic," +
            "bond:COC_ether_aliphatic__aromatic,bond:COC_ether_alkenyl,bond:COC_ether_aromatic,bond:COH_alcohol_aliphatic_generic," +
            "bond:COH_alcohol_alkene,bond:COH_alcohol_alkene_acyclic,bond:COH_alcohol_alkene_cyclic,bond:COH_alcohol_allyl," +
            "bond:COH_alcohol_aromatic,bond:COH_alcohol_aromatic_phenol,bond:COH_alcohol_benzyl,bond:COH_alcohol_diol_(1_1-)," +
            "bond:COH_alcohol_diol_(1_2-),bond:COH_alcohol_diol_(1_3-),bond:COH_alcohol_generic,bond:COH_alcohol_pri-alkyl," +
            "bond:COH_alcohol_sec-alkyl,bond:COH_alcohol_ter-alkyl,bond:CS_sulfide_di-,bond:CS_sulfide_dialkyl,bond:CS_sulfide," +
            "bond:CX_halide_alkenyl-Cl_acyclic,bond:CX_halide_alkenyl-Cl_dichloro_(1_1-),bond:CX_halide_alkenyl-X_acyclic," +
            "bond:CX_halide_alkenyl-X_acyclic_generic,bond:CX_halide_alkenyl-X_dihalo_(1_1-),bond:CX_halide_alkenyl-X_dihalo_(1_2-)," +
            "bond:CX_halide_alkenyl-X_generic,bond:CX_halide_alkenyl-X_trihalo_(1_1_2-),bond:CX_halide_alkyl-Cl_dichloro_(1_1-)," +
            "bond:CX_halide_alkyl-Cl_ethyl,bond:CX_halide_alkyl-Cl_trichloro_(1_1_1-),bond:CX_halide_alkyl-F_perfluoro_butyl," +
            "bond:CX_halide_alkyl-F_perfluoro_ethyl,bond:CX_halide_alkyl-F_perfluoro_hexyl,bond:CX_halide_alkyl-F_perfluoro_octyl," +
            "bond:CX_halide_alkyl-F_tetrafluoro_(1_1_1_2-),bond:CX_halide_alkyl-F_trifluoro_(1_1_1-),bond:CX_halide_alkyl-X_aromatic_alkane," +
            "bond:CX_halide_alkyl-X_aromatic_generic,bond:CX_halide_alkyl-X_benzyl_alkane,bond:CX_halide_alkyl-X_benzyl_generic,bond:CX_halide_alkyl-X_bicyclo[2_2_1]heptane," +
            "bond:CX_halide_alkyl-X_bicyclo[2_2_1]heptene,bond:CX_halide_alkyl-X_dihalo_(1_1-),bond:CX_halide_alkyl-X_dihalo_(1_2-),bond:CX_halide_alkyl-X_dihalo_(1_3)," +
            "bond:CX_halide_alkyl-X_ethyl,bond:CX_halide_alkyl-X_ethyl_generic,bond:CX_halide_alkyl-X_generic,bond:CX_halide_alkyl-X_primary," +
            "bond:CX_halide_alkyl-X_secondary,bond:CX_halide_alkyl-X_tertiary,bond:CX_halide_alkyl-X_tetrahalo_(1_1_2_2-),bond:CX_halide_alkyl-X_trihalo_(1_1_1-)," +
            "bond:CX_halide_alkyl-X_trihalo_(1_1_2-),bond:CX_halide_alkyl-X_trihalo_(1_2_3-),bond:CX_halide_allyl-Cl_acyclic,bond:CX_halide_allyl-X_acyclic," +
            "bond:CX_halide_aromatic-Cl_dichloro_pyridine_(1_2-),bond:CX_halide_aromatic-Cl_dichloro_pyridine_(1_3-),bond:CX_halide_aromatic-Cl_dichloro_pyridine_(1_4-)," +
            "bond:CX_halide_aromatic-Cl_dichloro_pyridine_(1_5-),bond:CX_halide_aromatic-Cl_trihalo_benzene_(1_2_4-),bond:CX_halide_aromatic-X_biphenyl," +
            "bond:CX_halide_aromatic-X_dihalo_benzene_(1_2-),bond:CX_halide_aromatic-X_dihalo_benzene_(1_3-),bond:CX_halide_aromatic-X_dihalo_benzene_(1_4-)," +
            "bond:CX_halide_aromatic-X_ether_aromatic_(Ph-O-Ph),bond:CX_halide_aromatic-X_ether_aromatic_(Ph-O-Ph)_generic,bond:CX_halide_aromatic-X_generic," +
            "bond:CX_halide_aromatic-X_halo_phenol,bond:CX_halide_aromatic-X_halo_phenol_meta,bond:CX_halide_aromatic-X_halo_phenol_ortho," +
            "bond:CX_halide_aromatic-X_halo_phenol_para,bond:CX_halide_aromatic-X_trihalo_benzene_(1_2_3-),bond:CX_halide_aromatic-X_trihalo_benzene_(1_3_5-)," +
            "bond:CX_halide_generic-X_dihalo_(1_2-),bond:N(=O)_nitrate_generic,bond:N(=O)_nitro_ab-acetylenic,bond:N(=O)_nitro_ab-unsaturated,bond:N(=O)_nitro_aromatic," +
            "bond:N(=O)_nitro_C,bond:N(=O)_nitro_N,bond:N[!C]_amino,bond:N=[N+]=[N-]_azide_aromatic,bond:N=[N+]=[N-]_azide_generic,bond:N=C=O_isocyanate_[O_S]," +
            "bond:N=C=O_isocyanate_generic,bond:N=C=O_isocyanate_thio,bond:N=N_azo_aliphatic_acyclic,bond:N=N_azo_aromatic,bond:N=N_azo_cyanamide,bond:N=N_azo_generic," +
            "bond:N=N_azo_oxy,bond:N=O_nitrite_neutral,bond:N=O_N-nitroso_alkyl_mono,bond:N=O_N-nitroso_dialkyl,bond:N=O_N-nitroso_generic,bond:N=O_N-nitroso," +
            "bond:NC=O_aminocarbonyl_generic,bond:NC=O_urea_generic,bond:NC=O_urea_thio,bond:NN_hydrazine_acyclic_(connect_noZ),bond:NN_hydrazine_alkyl_generic," +
            "bond:NN_hydrazine_alkyl_H,bond:NN_hydrazine_alkyl_H2,bond:NN_hydrazine_alkyl_HH,bond:NN_hydrazine_alkyl_HH2,bond:NN_hydrazine_alkyl_N(connect_Z=1)," +
            "bond:NN=N_triazene,bond:NO_amine_hyrdroxyl,bond:NO_amino_oxy_generic,bond:OZ_oxide_hyroxy,bond:OZ_oxide_peroxy,bond:OZ_oxide,bond:P(=O)N_phosphonamide," +
            "bond:P(=O)N_phosphoramide_diamidophosphate,bond:P(=O)N_phosphoramide_monoamidophosphate,bond:P(=O)N_phosphoramide_phosphotriamide," +
            "bond:P=C_phosphorane_generic,bond:P=O_phosphate_alkyl_ester,bond:P=O_phosphate_dithio,bond:P=O_phosphate_thio,bond:P=O_phosphate_thioate," +
            "bond:P=O_phosphate_trithio_phosphorothioate,bond:P=O_phosphate,bond:P=O_phosphonate_acid,bond:P=O_phosphonate_aliphatic_ester," +
            "bond:P=O_phosphonate_alkyl_ester,bond:P=O_phosphonate_cyano,bond:P=O_phosphonate_ester,bond:P=O_phosphonate_thio_dimethyl_methylphosphonothionate," +
            "bond:P=O_phosphonate_thio_acid,bond:P=O_phosphonate_thio_O_S-dimethyl_methylphosphonothioate,bond:P=O_phosphonate_thio_phosphonotrithioate," +
            "bond:P=O_phosphonate,bond:P=O_phosphorus_oxo,bond:PC_phosphine_organo_generic,bond:PC_phosphorus_organo_generic,bond:PO_phosphine_oxy," +
            "bond:PO_phosphine_oxy_generic,bond:PO_phosphite_generic,bond:PO_phosphite,bond:P~N_generic,bond:P~S_generic,bond:QQ(Q~O_S)_sulfhydride," +
            "bond:QQ(Q~O_S)_sulfide_di-,bond:QQ(Q~O_S)_sulfur_oxide,bond:quatN_alkyl_acyclic,bond:quatN_ammonium_inorganic,bond:quatN_b-carbonyl," +
            "bond:quatN_generic,bond:quatN_trimethyl_alkyl_acyclic,bond:quatP_phosphonium,bond:quatS,bond:S(=O)N_sulfonamide_ab-acetylenic," +
            "bond:S(=O)N_sulfonamide_ab-unsaturated,bond:S(=O)N_sulfonamide,bond:S(=O)N_sulfonylamide,bond:S(=O)O_sulfonate,bond:S(=O)O_sulfonicAcid_acyclic_(chain)," +
            "bond:S(=O)O_sulfonicAcid_anion,bond:S(=O)O_sulfonicAcid_cyclic_(ring),bond:S(=O)O_sulfonicAcid_generic,bond:S(=O)O_sulfonicEster_acyclic_(S-C(ring))," +
            "bond:S(=O)O_sulfonicEster_acyclic_S-C_(chain),bond:S(=O)O_sulfonicEster_aliphatic_(S-C),bond:S(=O)O_sulfonicEster_alkyl_O-C_(H=0)," +
            "bond:S(=O)O_sulfonicEster_alkyl_S-C,bond:S(=O)O_sulfonicEster_cyclic_S-(any_in_ring),bond:S(=O)O_sulfonyl_triflate,bond:S(=O)O_sulfuricAcid_generic," +
            "bond:S(=O)X_sulfonylhalide_fluoride,bond:S(=O)X_sulfonylhalide,bond:S=O_sulfonyl_a_b-acetylenic,bond:S=O_sulfonyl_a_b-unsaturated,bond:S=O_sulfonyl_cyanide," +
            "bond:S=O_sulfonyl_generic,bond:S=O_sulfonyl_S_(connect_Z=2),bond:S=O_sulfoxide,bond:S~N_generic,bond:Se~Q_selenium_oxo,bond:Se~Q_selenium_thio," +
            "bond:Se~Q_selenium_thioxo,bond:Se~Q_selenocarbon,bond:Se~Q_selenohalide,bond:X[any]_halide,bond:X[any_!C]_halide_inorganic," +
            "bond:X~Z_halide-[N_P]_heteroatom,bond:X~Z_halide-[N_P]_heteroatom_N,bond:X~Z_halide-[N_P]_heteroatom_N_generic,bond:X~Z_halide-[O_S]_heteroatom," +
            "bond:X~Z_halide_oxo,bond:metal_group_I_II_Ca_oxy_oxo,bond:metal_group_I_II_oxo,bond:metal_group_I_II_oxy,bond:metal_group_III_other_Al_generic," +
            "bond:metal_group_III_other_Al_halide,bond:metal_group_III_other_Al_organo,bond:metal_group_III_other_Al_oxo,bond:metal_group_III_other_Al_oxy," +
            "bond:metal_group_III_other_Bi_generic,bond:metal_group_III_other_Bi_halide,bond:metal_group_III_other_Bi_organo,bond:metal_group_III_other_Bi_oxo," +
            "bond:metal_group_III_other_Bi_oxy,bond:metal_group_III_other_Bi_sulfide,bond:metal_group_III_other_Bi_sulfide(II),bond:metal_group_III_other_generic," +
            "bond:metal_group_III_other_generic_oxo,bond:metal_group_III_other_generic_oxy,bond:metal_group_III_other_In_generic,bond:metal_group_III_other_In_halide," +
            "bond:metal_group_III_other_In_oxy,bond:metal_group_III_other_In_phosphide_arsenide,bond:metal_group_III_other_Pb_generic," +
            "bond:metal_group_III_other_Pb_halide,bond:metal_group_III_other_Pb_organo,bond:metal_group_III_other_Pb_oxo,bond:metal_group_III_other_Pb_oxy," +
            "bond:metal_group_III_other_Pb_sulfide,bond:metal_group_III_other_Pb_sulfide(II),bond:metal_group_III_other_Sn_generic,bond:metal_group_III_other_Sn_halide," +
            "bond:metal_group_III_other_Sn_organo,bond:metal_group_III_other_Sn_oxo,bond:metal_group_III_other_Sn_oxy,bond:metal_group_III_other_Sn_sulfide," +
            "bond:metal_group_III_other_Sn_sulfide(II),bond:metal_group_III_other_Th_generic,bond:metal_group_III_other_Th_halide,bond:metal_group_III_other_Th_oxo," +
            "bond:metal_metalloid_alkylSiloxane,bond:metal_metalloid_As_generic,bond:metal_metalloid_As_halide,bond:metal_metalloid_As_organo," +
            "bond:metal_metalloid_As_oxo,bond:metal_metalloid_As_oxy,bond:metal_metalloid_As_sulfide,bond:metal_metalloid_As_sulfide(II),bond:metal_metalloid_B_generic," +
            "bond:metal_metalloid_B_halide,bond:metal_metalloid_B_organo,bond:metal_metalloid_B_oxo,bond:metal_metalloid_B_oxy,bond:metal_metalloid_oxo," +
            "bond:metal_metalloid_oxy,bond:metal_metalloid_Sb_generic,bond:metal_metalloid_Sb_halide,bond:metal_metalloid_Sb_organo,bond:metal_metalloid_Sb_oxo," +
            "bond:metal_metalloid_Sb_oxy,bond:metal_metalloid_Sb_sulfide,bond:metal_metalloid_Sb_sulfide(II),bond:metal_metalloid_Si_generic," +
            "bond:metal_metalloid_Si_halide,bond:metal_metalloid_Si_organo,bond:metal_metalloid_Si_oxo,bond:metal_metalloid_Si_oxy,bond:metal_metalloid_Te_generic," +
            "bond:metal_metalloid_Te_halide,bond:metal_metalloid_Te_organo,bond:metal_metalloid_Te_oxo,bond:metal_metalloid_Te_oxy,bond:metal_metalloid_Te_sulfide," +
            "bond:metal_metalloid_Te_sulfide(II),bond:metal_metalloid_trimethylsilane,bond:metal_transition_Ag_oxy_oxo,bond:metal_transition_Cd_generic," +
            "bond:metal_transition_Cd_halide,bond:metal_transition_Cr_generic,bond:metal_transition_Cr_oxo,bond:metal_transition_Cr_oxy,bond:metal_transition_Cu_generic," +
            "bond:metal_transition_Cu_oxy_oxo,bond:metal_transition_Fe_generic,bond:metal_transition_Hg_generic,bond:metal_transition_Hg_halide," +
            "bond:metal_transition_Hg_organo,bond:metal_transition_Hg_oxo,bond:metal_transition_Hg_oxy,bond:metal_transition_Hg_sulfide," +
            "bond:metal_transition_Hg_sulfide(II),bond:metal_transition_Mn_generic,bond:metal_transition_Mn_oxy_oxo,bond:metal_transition_Mo_oxy_oxo," +
            "bond:metal_transition_Mo_sulfide,bond:metal_transition_oxo,bond:metal_transition_oxy,bond:metal_transition_Pt_generic,bond:metal_transition_Pt_halide,bond:metal_transition_Pt_nitrogen,bond:metal_transition_Pt_organo,bond:metal_transition_Pt_oxy,bond:metal_transition_Ti_generic,bond:metal_transition_Ti_organo,bond:metal_transition_Ti_oxo,bond:metal_transition_Ti_oxy,bond:metal_transition_Tl_halide,bond:metal_transition_V_generic,bond:metal_transition_V_oxo,bond:metal_transition_V_oxy,bond:metal_transition_W_generic,bond:metal_transition_W_oxo,bond:metal_transition_Zn_generic,bond:metal_transition_Zn_phosphide,chain:alkaneBranch_isopropyl_C3,chain:alkaneBranch_t-butyl_C4,chain:alkaneBranch_neopentyl_C5,chain:alkaneBranch_isohexyl_pentyl_3-methyl,chain:alkaneBranch_isooctyl_heptyl_3-methyl,chain:alkaneBranch_isooctyl_hexyl_2-ethyl,chain:alkaneBranch_isooctyl_hexyl_2-methyl,chain:alkaneBranch_isononyl_heptyl_2_5-methyl,chain:alkaneBranch_isononyl_pentyl_1_1_1_3-metyl,chain:alkaneBranch_isodecyl_octyl_1_2-methyl,chain:alkaneCyclic_ethyl_C2_(connect_noZ),chain:alkaneCyclic_propyl_C3,chain:alkaneCyclic_butyl_C4,chain:alkaneCyclic_pentyl_C5,chain:alkaneCyclic_hexyl_C6,chain:alkaneLinear_ethyl_C2(H_gt_1),chain:alkaneLinear_ethyl_C2_(connect_noZ_CN=4),chain:alkaneLinear_propyl_C3,chain:alkaneLinear_butyl_C4,chain:alkaneLinear_hexyl_C6,chain:alkaneLinear_octyl_C8,chain:alkaneLinear_decyl_C10,chain:alkaneLinear_dodedyl_C12,chain:alkaneLinear_tetradecyl_C14,chain:alkaneLinear_hexadecyl_C16,chain:alkaneLinear_stearyl_C18,chain:alkeneBranch_diene_2_6-octadiene,chain:alkeneBranch_diene_2_7-octadiene_(linalyl),chain:alkeneBranch_mono-ene_2-butene,chain:alkeneBranch_mono-ene_2-butene_2-propyl_(tiglate),chain:alkeneCyclic_diene_1_3-cyclohexadiene_C6,chain:alkeneCyclic_diene_1_5-cyclooctadiene,chain:alkeneCyclic_diene_cyclohexene,chain:alkeneCyclic_diene_cyclopentadiene,chain:alkeneCyclic_ethene_C_(connect_noZ),chain:alkeneCyclic_ethene_generic,chain:alkeneCyclic_triene_tropilidine,chain:alkeneLinear_diene_1_2-butene,chain:alkeneLinear_diene_1_3-butene,chain:alkeneLinear_diene_1_4-diene,chain:alkeneLinear_diene_linoleic_(C18),chain:alkeneLinear_mono-ene_2-hexene,chain:alkeneLinear_mono-ene_allyl,chain:alkeneLinear_mono-ene_ehtylene_terminal,chain:alkeneLinear_mono-ene_ethylene,chain:alkeneLinear_mono-ene_ethylene_generic,chain:alkeneLinear_mono-ene_oleic_(C18),chain:alkeneLinear_mono-ene_vinyl,chain:alkeneLinear_triene_linolenic_(C18),chain:alkyne_ethyne_generic,chain:aromaticAlkane_Ar-C_meta,chain:aromaticAlkane_Ar-C_ortho,chain:aromaticAlkane_Ar-C-Ar,chain:aromaticAlkane_Ph-C1_acyclic_connect_H_gt_1,chain:aromaticAlkane_Ph-C1_acyclic_connect_noDblBd,chain:aromaticAlkane_Ph-C1_acyclic_generic,chain:aromaticAlkane_Ph-1_4-C1_acyclic,chain:aromaticAlkane_Ph-C1-Ph,chain:aromaticAlkane_Ph-C2,chain:aromaticAlkane_Ph-C4,chain:aromaticAlkane_Ph-C6,chain:aromaticAlkane_Ph-C8,chain:aromaticAlkane_Ph-C9_nonylphenyl,chain:aromaticAlkane_Ph-C10,chain:aromaticAlkane_Ph-C12,chain:aromaticAlkane_Ph-C1_cyclic,chain:aromaticAlkene_Ph-C2_acyclic_generic,chain:aromaticAlkene_Ph-C2_styrene,chain:aromaticAlkene_Ph-C2,chain:aromaticAlkene_Ph-C3,chain:aromaticAlkene_Ph-C4_isocrotylbenzene,chain:aromaticAlkene_Ph-C4_phenylbutadiene,chain:aromaticAlkene_Ph-C2_cyclic,chain:oxy-alkaneLinear_ethyleneOxide_EO1,chain:oxy-alkaneLinear_ethylenOxide_EO1(O),chain:oxy-alkaneLinear_ethyleneOxide_EO2,chain:oxy-alkaneLinear_ethyleneOxide_EO3,chain:oxy-alkaneLinear_ethyleneOxide_EO4,chain:oxy-alkaneLinear_ethyleneOxide_EO6,chain:oxy-alkaneLinear_ethyleneOxide_EO8,chain:oxy-alkaneLinear_ethyleneOxide_EO10,chain:oxy-alkaneLinear_ethyleneOxide_EO12,chain:oxy-alkaneLinear_ethyleneOxide_EO14,chain:oxy-alkaneLinear_ethyleneOxide_EO16,chain:oxy-alkaneLinear_ethyleneOxide_EO18,chain:oxy-alkaneLinear_ethyleneOxide_EO20,chain:oxy-alkaneBranch_propyleneoxide_PO1,chain:oxy-alkaneBranch_propyleneoxide_PO2,chain:oxy-alkaneBranch_propyleneoxide_PO3,chain:oxy-alkaneBranch_propyleneoxide_PO4,chain:oxy-alkaneBranch_propyleneoxide_PO6,chain:oxy-alkaneBranch_propyleneoxide_PO8,chain:oxy-alkaneBranch_propyleneoxide_PO10,chain:oxy-alkaneLinear_carboxylicEster_AEOC,chain:oxy-alkaneLinear_sulfuricEster_AEOS,group:aminoAcid_aminoAcid_generic,group:aminoAcid_alanine,group:aminoAcid_arginine,group:aminoAcid_asparagine,group:aminoAcid_aspartic_acid,group:aminoAcid_cysteine,group:aminoAcid_glutamic_acid,group:aminoAcid_glutamine,group:aminoAcid_glycine,group:aminoAcid_histidine,group:aminoAcid_isoleucine,group:aminoAcid_leucine,group:aminoAcid_lysine,group:aminoAcid_methionine,group:aminoAcid_phenylalanine,group:aminoAcid_proline,group:aminoAcid_serine,group:aminoAcid_threonine,group:aminoAcid_tryptophan,group:aminoAcid_tyrosine,group:aminoAcid_valine,group:carbohydrate_aldohexose,group:carbohydrate_aldopentose,group:carbohydrate_hexofuranose_hexulose,group:carbohydrate_hexofuranose,group:carbohydrate_hexopyranose_2-deoxy,group:carbohydrate_hexopyranose_fructose,group:carbohydrate_hexopyranose_generic,group:carbohydrate_hexopyranose_glucose,group:carbohydrate_hexopyranose_maltose,group:carbohydrate_inositol,group:carbohydrate_ketohexose,group:carbohydrate_ketopentose,group:carbohydrate_pentofuranose_2-deoxy,group:carbohydrate_pentofuranose,group:carbohydrate_pentopyranose,group:ligand_path_4_bidentate_aminoacetaldehyde,group:ligand_path_4_bidentate_aminoacetate,group:ligand_path_4_bidentate_aminoethanol,group:ligand_path_4_bidentate_bipyridyl,group:ligand_path_4_bidentate_ethylenediamine,group:ligand_path_4_macrocycle_tetrazacyclododecane,group:ligand_path_4_macrocycle_triethylenetriamine,group:ligand_path_4_polydentate,group:ligand_path_4_polydentate_EDTA,group:ligand_path_4_polydentate_NTA,group:ligand_path_4_tridentate,group:ligand_path_4-5_macrocycle_tetrazacyclotetradecane,group:ligand_path_4-5_tridentate,group:ligand_path_5_bidentate_ACAC,group:ligand_path_5_bidentate_aminopropanal,group:ligand_path_5_bidentate_bipyridylmethyl,group:ligand_path_5_bidentate_bipyrrolidilmethyl,group:ligand_path_5_bidentate_diformamide,group:ligand_path_5_bidentate_malonate,group:ligand_path_5_bidentate_propandiamine,group:ligand_path_5_bidentate_propanolamine,group:ligand_path_5_macrocycle,group:ligand_path_5_tridentate,group:ligand_path_5_tridentate_3-hydroxycadaverine,group:ligand_path_5-7_bidentate,group:nucleobase_adenine,group:nucleobase_cytosine,group:nucleobase_guanine,group:nucleobase_guanine_7-methyl,group:nucleobase_thymine,group:nucleobase_uracil,group:nucleobase_hypoxanthine,group:nucleobase_xanthine_purine-2_6-dione,ring:aromatic_benzene,ring:aromatic_biphenyl,ring:aromatic_phenyl,ring:fused_[5_6]_indane,ring:fused_[5_6]_indene,ring:fused_[5_7]_azulene,ring:fused_[6_6]_naphthalene,ring:fused_[6_6]_tetralin,ring:fused_PAH_acenaphthylene,ring:fused_PAH_anthanthrene,ring:fused_PAH_anthracene,ring:fused_PAH_benz(a)anthracene,ring:fused_PAH_benzophenanthrene,ring:fused_PAH_fluorene,ring:fused_PAH_phenanthrene,ring:fused_PAH_pyrene,ring:fused_steroid_generic_[5_6_6_6],ring:hetero_[3]_N_aziridine,ring:hetero_[3]_O_epoxide,ring:hetero_[3]_Z_generic,ring:hetero_[4]_N_azetidine,ring:hetero_[4]_N_beta_lactam,ring:hetero_[4]_O_oxetane,ring:hetero_[4]_Z_generic,ring:hetero_[5]_N_imidazole,ring:hetero_[5]_N_pyrazole,ring:hetero_[5]_N_pyrrole,ring:hetero_[5]_N_pyrrole_generic,ring:hetero_[5]_N_pyrrolidone_(2-),ring:hetero_[5]_N_tetrazole,ring:hetero_[5]_N_triazole_(1_2_3-),ring:hetero_[5]_N_triazole_(1_2_4-),ring:hetero_[5]_N_triazole_(1_3_4-),ring:hetero_[5]_N_O_isoxazole,ring:hetero_[5]_N_O_oxazole,ring:hetero_[5]_N_S_isothiazole,ring:hetero_[5]_N_S_thiadiazole_(1_3_4-),ring:hetero_[5]_N_S_thiazole,ring:hetero_[5]_O_dioxolane_(1_3-),ring:hetero_[5]_O_furan,ring:hetero_[5]_O_furan_a-nitro,ring:hetero_[5]_O_oxolane,ring:hetero_[5]_S_thiophene,ring:hetero_[5]_Z_1_2_3_4-Z,ring:hetero_[5]_Z_1_2_3-Z,ring:hetero_[5]_Z_1_2_4_1_3_4-Z,ring:hetero_[5]_Z_1_2-Z,ring:hetero_[5]_Z_1_3-Z,ring:hetero_[5]_Z_1-Z,ring:hetero_[5_5]_N_pyrrolizidine,ring:hetero_[5_5]_Z_generic,ring:hetero_[5_5_6]_O_aflatoxin_generic,ring:hetero_[5_6]_N_benzimidazole,ring:hetero_[5_6]_N_indazole,ring:hetero_[5_6]_N_indole,ring:hetero_[5_6]_N_isoindole_1_3-dione,ring:hetero_[5_6]_N_isoindole_1-one,ring:hetero_[5_6]_N_purine,ring:hetero_[5_6]_N_S_benzothiazole_(1_3-),ring:hetero_[5_6]_O_benzodioxole_(1_3-),ring:hetero_[5_6]_O_benzofuran,ring:hetero_[5_6]_Z_generic,ring:hetero_[5_7]_Z_generic,ring:hetero_[6]_N_diazine_(1_2-)_generic,ring:hetero_[6]_N_diazine_(1_3-)_generic,ring:hetero_[6]_N_piperazine,ring:hetero_[6]_N_piperidine,ring:hetero_[6]_N_pyrazine,ring:hetero_[6]_N_pyridazine,ring:hetero_[6]_N_pyridine,ring:hetero_[6]_N_pyridine_generic,ring:hetero_[6]_N_pyrimidine,ring:hetero_[6]_N_pyrimidine_2_4-dione,ring:hetero_[6]_N_tetrazine_(1_2_3_4-),ring:hetero_[6]_N_tetrazine_generic,ring:hetero_[6]_N_triazine_(1_2_3-),ring:hetero_[6]_N_triazine_(1_2_4-),ring:hetero_[6]_N_triazine_(1_3_5-),ring:hetero_[6]_N_triazine_generic,ring:hetero_[6]_N_O_1_4-oxazine_generic,ring:hetero_[6]_N_O_1_4-oxazine_morpholine,ring:hetero_[6]_O_dioxane_(1_4-)_generic,ring:hetero_[6]_O_pyran_generic,ring:hetero_[6]_Z_1-,ring:hetero_[6]_Z_1_2-,ring:hetero_[6]_Z_1_2_3-,ring:hetero_[6]_Z_1_2_3_4-,ring:hetero_[6]_Z_1_2_3_5-,ring:hetero_[6]_Z_1_2_4-,ring:hetero_[6]_Z_1_2_4_5-,ring:hetero_[6]_Z_1_3-,ring:hetero_[6]_Z_1_3_5-,ring:hetero_[6]_Z_1_4-,ring:hetero_[6]_Z_generic,ring:hetero_[6_5_6]_N_carbazole,ring:hetero_[6_5_6]_O_benzofuran_dibenzo,ring:hetero_[6_6]_N_isoquinoline,ring:hetero_[6_6]_N_pteridine,ring:hetero_[6_6]_N_pteridine_generic,ring:hetero_[6_6]_N_quinazoline,ring:hetero_[6_6]_N_quinoline,ring:hetero_[6_6]_N_quinoxaline,ring:hetero_[6_6]_O_benzodioxin_(1_4-),ring:hetero_[6_6]_O_benzopyran,ring:hetero_[6_6]_O_benzopyrone_(1_2-),ring:hetero_[6_6]_O_benzopyrone_(1_4-),ring:hetero_[6_6]_Z_generic,ring:hetero_[6_6_6]_N_acridine,ring:hetero_[6_6_6]_N_pteridine_flavin_generic,ring:hetero_[6_6_6]_N_S_phenothiazine,ring:hetero_[6_6_6]_O_benzopyran_dibenzo[b_d],ring:hetero_[6_6_6]_O_benzopyran_dibenzo[b_e],ring:hetero_[6_7]_N_benzodiazepine_(1_4-),ring:hetero_[7]_generic_1_2-Z,ring:hetero_[7]_generic_1_3-Z,ring:hetero_[7]_generic_1_4-Z,ring:hetero_[7]_generic_1-Z,ring:hetero_[7]_N_azepine_generic,ring:hetero_[7]_N_diazepine_(1_4-),ring:hetero_[7]_O_oxepin,ring:polycycle_bicyclo_[2.1.1]heptane,ring:polycycle_bicyclo_[2.1.1]hexane,ring:polycycle_bicyclo_[2.1.1]hexane_5-oxabicyclo,ring:polycycle_bicyclo_[2.2.2]octane,ring:polycycle_bicyclo_[2.2.2]octatriene,ring:polycycle_bicyclo_[3.2.1]octane,ring:polycycle_bicyclo_[3.2.2]nonane,ring:polycycle_bicyclo_[3.3.1]nonane,ring:polycycle_bicyclo_[3.3.2]decane,ring:polycycle_bicyclo_[4.2.0]octadiene,ring:polycycle_bicyclo_[4.3.1]decane,ring:polycycle_bicyclo_[4.4.1]undecane,ring:polycycle_bicyclo_[5.1.0]octadiene,ring:polycycle_bicyclo_[5.4.1]dodecane,ring:polycycle_bicyclo_propene,ring:polycycle_spiro_[2.2]pentane,ring:polycycle_spiro_[2.5]octane,ring:polycycle_spiro_[4.5]decane,ring:polycycle_spiro_1_4-dioxaspiro[4.5]decane,ring:polycycle_tricyclo_[3.5.5]_cyclopropa[cd]pentalene,ring:polycycle_tricyclo_[3.7.7]bullvalene,ring:polycycle_tricyclo_[3.7.7]semibullvalene,ring:polycycle_tricyclo_adamantane,ring:polycycle_tricyclo_benzvalene";

//    final String toxprintHeaderValues = "atom:element_main_group,atom:element_metal_group_I_II,atom:element_metal_group_III," +
//            "atom:element_metal_metalloid";

    List<String> toxprintHeader = Arrays.asList(toxprintHeaderValues.split(","));

    private static final Path EXCEL_TEMP_FILE_PATH;

    static {
        //EXCEL_TEMP_FILE_PATH = Paths.get(System.getProperty("user.dir")).resolve("excelStaging");
        EXCEL_TEMP_FILE_PATH = Paths.get(System.getProperty("java.io.tmpdir")).resolve("excelStaging");
        if (!Files.exists(EXCEL_TEMP_FILE_PATH)) {
            try {
                Files.createDirectories(EXCEL_TEMP_FILE_PATH);
            } catch (IOException e) {
                log.error("Could not create temp page", e);
            }
            log.info("Created temp folder = {}", EXCEL_TEMP_FILE_PATH);
            TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy(EXCEL_TEMP_FILE_PATH.toFile()));

        }
    }

    public MediaType getExcelContentType() {
        // we can also use application/vnd.ms-excel, https://stackoverflow.com/questions/7076042/what-mime-type-should-i-use-for-csv
        log.debug("media type = {}", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public String getExcelFilename() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh_mm_ss");
        String excelFileName = "CCD-Batch-Search_" + formatter.format(LocalDateTime.now()) + ".xlsx";

        log.debug("excel file name = {}", excelFileName);

        return excelFileName;
    }

    protected byte[] getContents() {
        Long startTime = System.currentTimeMillis();
        log.debug("start getContents ");

        byte[] bytes;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            // If streaming Apache Poi was used then dispose of temp files
            if (workbook instanceof SXSSFWorkbook) {
                workbook.dispose();
            }
        } catch (IOException e) {
            log.error("Failed to convert Excel to byte array", e);
            throw new RuntimeException("Failed to convert Excel to byte array: " + e.getMessage());
        }
        Long endTime = System.currentTimeMillis();
        log.info("end getContents for Excel->" + (endTime - startTime) / 1000 + " seconds.");
        return bytes;

    }

    protected CellStyle getHeaderStyle() {

        CellStyle style = workbook.createCellStyle();

        style.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        //style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        //style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        // style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        // style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        // style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        //style.setAlignment(CellStyle.ALIGN_CENTER);

        // font
        Font font= workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(false);

        style.setFont(font);

        return style;

    }

    protected CellStyle getNormalStyle() {

        CellStyle style = workbook.createCellStyle();
        //style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        ///style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        //style.setAlignment(CellStyle.ALIGN_CENTER);

        // font
        Font font= workbook.createFont();
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(false);
        font.setItalic(false);

        style.setFont(font);

        return style;

    }

    protected CellStyle getNumericStyle() {

        CellStyle style = workbook.createCellStyle();
        //style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        ///style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        //style.setAlignment(CellStyle.ALIGN_CENTER);

        // font
        Font font= workbook.createFont();
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(false);
        font.setItalic(false);

        style.setFont(font);

        // data format
//        DataFormat format = workbook.createDataFormat();
//        style.setDataFormat(format.getFormat("#.###############"));

        return style;

    }

    protected CellStyle getHyperLinkStyle(){
        CellStyle style = workbook.createCellStyle();
        //style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        ///style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        //style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        //style.setAlignment(CellStyle.ALIGN_CENTER);

        // font
        Font font= workbook.createFont();
        font.setFontHeightInPoints((short)10);
        font.setFontName("Arial");
        font.setColor(IndexedColors.BLUE.getIndex());
        font.setBold(false);
        font.setItalic(false);

        style.setFont(font);

        return style;
    }

    protected void initialize() {
        // set default value for empty value
        setEmptyValue(" ");

        headers = new HashMap<>();
        sheetCurrentRow = new HashMap<>();

        workbook = new SXSSFWorkbook(ROWS_IN_MEMORY);

        // Create style to be used by all sheets
        dataRowStyle = getNormalStyle();
        hyperLinkStyle = getHyperLinkStyle();
        numericDataStyle = getNumericStyle();

        // Toxcast assays sheet
        assayColumns = null;  // these are search words which has matching records
        toxcastLookup = null ;

        // for chemical lists export with excel
        chemlistLookup = null;
        selectedChemicalLists = null;

        // Synonyms
        chemicalSynonyms = null;
        relatedSubLookup = null;
        headerWithToxprint = null;


//        https://stackoverflow.com/questions/47477912/apache-poi-how-to-use-addignorederrors-functionality-in-sxssfsheet

//        Field _sh = null;
//        try {
//            _sh = SXSSFSheet.class.getDeclaredField("_sh");
//            _sh.setAccessible(true);
//            XSSFSheet xssfsheet = (XSSFSheet)_sh.get(sheet);
//            xssfsheet.addIgnoredErrors(new CellRangeAddress(0, 99, 0, 0), IgnoredErrorType.NUMBER_STORED_AS_TEXT);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }

    }

    protected void addHeader(List<String> mandatory, List<String> selectedItems, List<String> searchWords, HashMap<String, List<SearchWithChemicalDetails>> results) {

        //List<String> selectedItems = Arrays.asList(selected);
        List<String> combine;

        if (selectedItems.contains("METGRAG_INPUT_FILE")) {
            // create columns for metfrag on main data sheet and then all rest of the part
            combine = Stream.concat(List.of("INPUT", "FOUND_BY", "DTXCID_INDIVIDUAL_COMPONENT", "FORMULA_INDIVIDUAL_COMPONENT", "SMILES_INDIVIDUAL_COMPONENT", "MAPPED_DTXSID", "PREFERRED_NAME_DTXSID", "CASRN_DTXSID", "FORMULA_MAPPED_DTXSID", "SMILES_MAPPED_DTXSID", "MS_READY_SMILES", "INCHI_STRING_DTXCID", "INCHIKEY_DTXCID", "MONOISOTOPIC_MASS_DTXCID").stream(),
                    selectedItems.stream()).collect(Collectors.toList());
        } else {
            combine = Stream.concat(mandatory.stream(), selectedItems.stream()).collect(Collectors.toList());
        }

        // Check if selectedItems contains MS_READY_SMILES and add two headers MS_READY_MASS,MS_READY_FORMULAE

        if (selectedItems.contains("MS_READY_SMILES")) {
            combine = Stream.concat(List.of("INPUT", "FOUND_BY",  "PREFERRED_NAME").stream(),
                    selectedItems.stream()).collect(Collectors.toList());
            combine.addAll(Arrays.asList("MS_READY_MASS","MS_READY_FORMULAE"));
        } else {
            combine = Stream.concat(mandatory.stream(), selectedItems.stream()).collect(Collectors.toList());
        }

        // cover sheet
        sheet = workbook.createSheet(COVER_SHEET);

        // Main sheet
        sheet = workbook.createSheet(MAIN_DATA_SHEET);
        // remove header belong to extended sheets
        combine.removeAll(List.of("ABSTRACT_SHIFTER", "SYNONYM_IDENTIFIER","RELATED_RELATIONSHIP", "ASSOCIATED_TOXCAST_ASSAYS", "TOXVAL_DETAILS", "TOXREF_DETAILS","CHEMICAL_PROPERTIES_DETAILS"));

        // Add chemical list as header columns
        if(selectedChemicalLists != null && selectedChemicalLists.length > 0){
            combine.addAll(Arrays.asList(selectedChemicalLists));
            log.debug("added {} chemical lists to header", selectedChemicalLists.length);
        }

        if(combine.contains("TOXPRINTS_CHEMOTYPER")){
            // remove from header and add actual finger print headers added to excel file
            combine.remove("TOXPRINTS_CHEMOTYPER");

            List<String> headerWithToxprint = new ArrayList<> ();
            headerWithToxprint.addAll(toxprintHeader);

            combine.addAll(headerWithToxprint);

            this.headerWithToxprint = headerWithToxprint;


        }else{
            this.headerWithToxprint = null;
        }

        addHeaderColumns(sheet, combine);

        // header
        headers.put(MAIN_DATA_SHEET, combine);

        if (selectedItems.contains("ABSTRACT_SHIFTER")) {
            sheet = workbook.createSheet(ABSTRACT_SHIFTER);
            combine = ABSTRACT_SHIFTER_COLS;
            addHeaderColumns(sheet, combine);
            headers.put(ABSTRACT_SHIFTER, combine);
        }

        if (selectedItems.contains("SYNONYM_IDENTIFIER")) {
            sheet = workbook.createSheet(SYNONYM_IDENTIFIER);
            combine = SYNONYM_IDENTIFIER_COLS;
            addHeaderColumns(sheet, combine);
            headers.put(SYNONYM_IDENTIFIER, combine);
        }

        if (selectedItems.contains("RELATED_RELATIONSHIP")) {
            sheet = workbook.createSheet(RELATED_RELATIONSHIP);
            combine = RELATED_RELATIONSHIP_COLS;
            addHeaderColumns(sheet, combine);
            headers.put(RELATED_RELATIONSHIP, combine);
        }

        if (selectedItems.contains("ASSOCIATED_TOXCAST_ASSAYS")) {
            sheet = workbook.createSheet(ASSOCIATED_TOXCAST_ASSAYS);
            combine = Stream.concat(ASSOCIATED_TOXCAST_ASSAYS_COLS.stream(), assayColumns.stream()).collect(Collectors.toList());
            addHeaderColumns(sheet, combine);
            headers.put(ASSOCIATED_TOXCAST_ASSAYS, combine);
        }

        if (selectedItems.contains("TOXVAL_DETAILS")) {
            sheet = workbook.createSheet(TOXVAL_DETAILS);
            combine = TOXVAL_IDENTIFIER_COLS;
            addHeaderColumns(sheet, combine);
            headers.put(TOXVAL_DETAILS, combine);
        }

        if (selectedItems.contains("TOXREF_DETAILS")) {
            sheet = workbook.createSheet(TOXREF_DETAILS);
            combine = TOXREF_IDENTIFIER_COLS;
            addHeaderColumns(sheet, combine);
            headers.put(TOXREF_DETAILS, combine);
        }

        if (selectedItems.contains("CHEMICAL_PROPERTIES_DETAILS")) {
            sheet = workbook.createSheet(CHEMICAL_PROPERTIES_DETAILS);
            combine = CHEMICAL_PROPERTIES_DETAILS_COLS;
            addHeaderColumns(sheet, combine);
            headers.put(CHEMICAL_PROPERTIES_DETAILS, combine);
        }

    }


    private void addHeaderColumns(Sheet sheet, List<String> columns) {
        // CellStyle headerStyle = getHeaderStyle();

        sheetCurrentRow.put(sheet.getSheetName(), 1);

        Row row = sheet.createRow(0);

        //row.setRowStyle(headerStyle);
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(columns.get(i));
            cell.setCellStyle(headerStyle);
        }
    }

    // Currently error row is only added to first page
    protected void addNotFoundRow(String searchWord) {

        // Get sheet
        Sheet sheet = workbook.getSheet(MAIN_DATA_SHEET);

        Integer currentRow = sheetCurrentRow.get(MAIN_DATA_SHEET);

        Row row = sheet.createRow(currentRow++);

        sheetCurrentRow.put(MAIN_DATA_SHEET, currentRow);

        // INPUT column
        Cell cell = row.createCell(0);
        cell.setCellValue(searchWord);

        // FOUND_BY column
        cell = row.createCell(1);
        cell.setCellValue(Validators.getDataNotFoundMsg(searchWord));

    }

    protected void addNotFoundRowForMsReady(String searchWord) {

        // Get sheet
        Sheet sheet = workbook.getSheet(MAIN_DATA_SHEET);

        Integer currentRow = sheetCurrentRow.get(MAIN_DATA_SHEET);

        Row row = sheet.createRow(currentRow++);

        sheetCurrentRow.put(MAIN_DATA_SHEET, currentRow);

        // INPUT column
        Cell cell = row.createCell(0);
        cell.setCellValue(searchWord);

        // FOUND_BY column
        cell = row.createCell(1);
        cell.setCellValue("Searched by Ms Ready Formula : Found 0 results");

    }

    protected void addDataRow(String searchWord, SearchWithChemicalDetails details) {
        for(String tab : headers.keySet()){
            switch (tab){
                case COVER_SHEET: break; // this tab is updated as individual excel export type
                case SYNONYM_IDENTIFIER: updateSynonymSheet(details, chemicalSynonyms); break;
                case RELATED_RELATIONSHIP: updateRelatedSheet(details, relatedSubLookup); break;
                case TOXVAL_DETAILS: updateToxvalSheet(details, toxvalBatchSearch); break;
                case TOXREF_DETAILS: updateToxrefSheet(details, toxrefBatchSearch); break;
                case ABSTRACT_SHIFTER: updateMainDataSheet(searchWord, details, tab); break;
                case ASSOCIATED_TOXCAST_ASSAYS: break; // this tab is updated as individual excel export type
                case CHEMICAL_PROPERTIES_DETAILS: break; // this tab is updated as individual excel export type
                case MAIN_DATA_SHEET: updateMainDataSheet(searchWord, details, tab);
            }
        }
    }

    protected void updateRelatedSheet(SearchWithChemicalDetails details, Hashtable<String, List<RelatedSubstance>> relatedSubLookup){
        log.debug("updating related relationship sheet for dtxsid={} ", details.getDtxsid());

        //CellStyle dataRowStyle = getNormalStyle();

        Sheet sheet = workbook.getSheet(RELATED_RELATIONSHIP);
        Integer currentRow = sheetCurrentRow.get(RELATED_RELATIONSHIP);

        Cell cell;
        // getting synonym from lookup
        List<RelatedSubstance> substances = relatedSubLookup.get(details.getDtxsid());
        log.debug("{} has {} related substances", details.getDtxsid(), substances.size());

        for(RelatedSubstance substance : substances){

            Row row = sheet.createRow(currentRow++);
            row.setRowStyle(dataRowStyle);

            // INPUT
            cell = row.createCell(0);
            cell.setCellValue(details.getSearchWord());

            // DTXSID
            cell = row.createCell(1);
            cell.setCellValue(substance.getDtxsid());

            // PREFERRED NAME
            cell = row.createCell(2);
            cell.setCellValue(substance.getPreferredName());

            // HAS_RELATIONSHIP_WITH
            cell = row.createCell(3);
            cell.setCellValue(substance.getRelationship());

            // RELATED_DTXSID
            cell = row.createCell(4);
            cell.setCellValue(substance.getRealtedDtxsid());

            // RELATED_PREFERRED_NAME
            cell = row.createCell(5);
            cell.setCellValue(substance.getRelatedPreferredName());

            // RELATED_CASRN
            cell = row.createCell(6);
            cell.setCellValue(substance.getRelatedCasrn());

        }
        sheetCurrentRow.put(RELATED_RELATIONSHIP, currentRow);
    }

    private void updateSynonymSheet(SearchWithChemicalDetails details, Hashtable<String, ChemicalSynonym> chemicalSynonyms) {
        log.debug("updating synonyms sheet for dtxsid={} ", details.getDtxsid());

        //CellStyle dataRowStyle = getNormalStyle();

        Sheet sheet = workbook.getSheet(SYNONYM_IDENTIFIER);
        Integer currentRow = sheetCurrentRow.get(SYNONYM_IDENTIFIER);

        Row row = sheet.createRow(currentRow++);
        sheetCurrentRow.put(SYNONYM_IDENTIFIER, currentRow);
        row.setRowStyle(dataRowStyle);

        Cell cell;
        // getting synonym from lookup
        ChemicalSynonym synonym = chemicalSynonyms.get(details.getDtxsid());

        if(synonym != null){
            // log.debug("synonyms = {} ", synonym);

            // SEARCHED_CHEMICAL
            cell = row.createCell(0);
            cell.setCellValue(details.getPreferredName());

            // IDENTIFIERS
            cell = row.createCell(1);
            cell.setCellValue(synonym.getSynonyms());

            // PC-CODE
            cell = row.createCell(2);
            cell.setCellValue(synonym.getPcCode());
        }
    }

    private void updateToxrefSheet(SearchWithChemicalDetails details, Hashtable<String, List<ToxrefBatchSearch>> toxrefBatchSearchItems) {
        log.debug("updating toxref batch search  sheet for dtxsid={} ", details.getDtxsid());

        List<ToxrefBatchSearch> toxrefBatchSearches = toxrefBatchSearchItems.get((details.getDtxsid()));
        if (toxrefBatchSearches != null && toxrefBatchSearches.size() > 0) {
            log.debug(" dtxsid={} has {} toxrefs ", details.getDtxsid(), toxrefBatchSearches.size());
            Sheet sheet = workbook.getSheet(TOXREF_DETAILS);
            Integer currentRow = sheetCurrentRow.get(TOXREF_DETAILS);

            Cell cell;
            for(ToxrefBatchSearch toxrefBatchSearch: toxrefBatchSearches){
                Row row = sheet.createRow(currentRow++);
                sheetCurrentRow.put(TOXREF_DETAILS, currentRow);
                row.setRowStyle(dataRowStyle);


                cell = row.createCell(0);
                cell.setCellValue(toxrefBatchSearch.getStudyId());

                cell = row.createCell(1);
                cell.setCellValue(toxrefBatchSearch.getName());

                cell = row.createCell(2);
                cell.setCellValue(toxrefBatchSearch.getDtxsid());

                cell = row.createCell(3);
                cell.setCellValue(toxrefBatchSearch.getCasrn());

                cell = row.createCell(4);
                cell.setCellValue(toxrefBatchSearch.getStudySource());

                cell = row.createCell(5);
                cell.setCellValue(toxrefBatchSearch.getStudySourceId());

                cell = row.createCell(6);
                cell.setCellValue(toxrefBatchSearch.getCitation());

                cell = row.createCell(7);
                cell.setCellValue(toxrefBatchSearch.getStudyYear());

                cell = row.createCell(8);
                cell.setCellValue(toxrefBatchSearch.getStudyType());

                cell = row.createCell(9);
                cell.setCellValue(toxrefBatchSearch.getStudyTypeGuideline());

                cell = row.createCell(10);
                cell.setCellValue(toxrefBatchSearch.getSpecies());

                cell = row.createCell(11);
                cell.setCellValue(toxrefBatchSearch.getStrainGroup());

                cell = row.createCell(12);
                cell.setCellValue(toxrefBatchSearch.getStrain());

                cell = row.createCell(13);
                cell.setCellValue(toxrefBatchSearch.getAdminRoute());

                cell = row.createCell(14);
                cell.setCellValue(toxrefBatchSearch.getAdminMethod());

                cell = row.createCell(15);
                cell.setCellValue(toxrefBatchSearch.getDoseDuration());

                cell = row.createCell(16);
                cell.setCellValue(toxrefBatchSearch.getDoseDurationUnit());

                cell = row.createCell(17);
                cell.setCellValue(toxrefBatchSearch.getDoseStart());

                cell = row.createCell(18);
                cell.setCellValue(toxrefBatchSearch.getDoseStartUnit());

                cell = row.createCell(19);
                cell.setCellValue(toxrefBatchSearch.getDoseEnd());

                cell = row.createCell(20);
                cell.setCellValue(toxrefBatchSearch.getDoseEndUnit());

                cell = row.createCell(21);
                cell.setCellValue(toxrefBatchSearch.getDosePeriod());

                cell = row.createCell(22);
                cell.setCellValue(toxrefBatchSearch.getDoseLevel());

                cell = row.createCell(23);
                cell.setCellValue(toxrefBatchSearch.getConc());

                cell = row.createCell(24);
                cell.setCellValue(toxrefBatchSearch.getConcUnit());

                cell = row.createCell(25);
                cell.setCellValue(toxrefBatchSearch.getVehicle());

                cell = row.createCell(26);
                cell.setCellValue(toxrefBatchSearch.getDoseComment());

                cell = row.createCell(27);
                cell.setCellValue(toxrefBatchSearch.getDoseAdjusted());

                cell = row.createCell(28);
                cell.setCellValue(toxrefBatchSearch.getDoseAdjustedUnit());

                cell = row.createCell(29);
                cell.setCellValue(toxrefBatchSearch.getSex());

                cell = row.createCell(30);
                cell.setCellValue(toxrefBatchSearch.getGeneration());

                cell = row.createCell(31);
                cell.setCellValue(toxrefBatchSearch.getLifeStage());

                cell = row.createCell(32);
                cell.setCellValue(toxrefBatchSearch.getNumAnimals());

                cell = row.createCell(33);
                cell.setCellValue(toxrefBatchSearch.getTgComment());

                cell = row.createCell(34);
                cell.setCellValue(toxrefBatchSearch.getEndpointCategory());

                cell = row.createCell(35);
                cell.setCellValue(toxrefBatchSearch.getEndpointType());

                cell = row.createCell(36);
                cell.setCellValue(toxrefBatchSearch.getEndpointTarget());

                cell = row.createCell(37);
                cell.setCellValue(toxrefBatchSearch.getEffectDesc());

                cell = row.createCell(38);
                cell.setCellValue(toxrefBatchSearch.getEffectDescFree());

                cell = row.createCell(39);
                cell.setCellValue(toxrefBatchSearch.getCancerRelated());

                cell = row.createCell(40);
                cell.setCellValue(toxrefBatchSearch.getTargetSite());

                cell = row.createCell(41);
                cell.setCellValue(toxrefBatchSearch.getDirection());

                cell = row.createCell(42);
                cell.setCellValue(toxrefBatchSearch.getEffectComment());

                cell = row.createCell(43);
                cell.setCellValue(toxrefBatchSearch.getTreatmentRelated());

                cell = row.createCell(44);
                cell.setCellValue(toxrefBatchSearch.getCriticalEffect());

                cell = row.createCell(45);
                cell.setCellValue(toxrefBatchSearch.getSampleSize());

                cell = row.createCell(46);
                cell.setCellValue(toxrefBatchSearch.getEffectVal());

                cell = row.createCell(47);
                cell.setCellValue(toxrefBatchSearch.getEffectValUnit());

                cell = row.createCell(48);
                cell.setCellValue(toxrefBatchSearch.getEffectVar());

                cell = row.createCell(49);
                cell.setCellValue(toxrefBatchSearch.getEffectVarType());

                cell = row.createCell(50);
                cell.setCellValue(toxrefBatchSearch.getTime());

                cell = row.createCell(51);
                cell.setCellValue(toxrefBatchSearch.getTimeUnit());

                cell = row.createCell(52);
                cell.setCellValue(toxrefBatchSearch.getNoQuantDataReported());


            }
        }
    }


       private void updateToxvalSheet(SearchWithChemicalDetails details, Hashtable<String, List<ToxvalBatchSearch>> toxvalBatchSearchItems) {
        log.debug("updating toxval batch search  sheet for dtxsid={} ", details.getDtxsid());

        List<ToxvalBatchSearch> toxvalBatchSearches= toxvalBatchSearchItems.get((details.getDtxsid()));
        if(toxvalBatchSearches != null && toxvalBatchSearches.size() > 0){
            log.debug(" dtxsid={} has {} toxvals ", details.getDtxsid(), toxvalBatchSearches.size());
            Sheet sheet = workbook.getSheet(TOXVAL_DETAILS);
            Integer currentRow = sheetCurrentRow.get(TOXVAL_DETAILS);

            Cell cell;
            for(ToxvalBatchSearch toxvalBatchSearch: toxvalBatchSearches){
                Row row = sheet.createRow(currentRow++);
                sheetCurrentRow.put(TOXVAL_DETAILS, currentRow);
                row.setRowStyle(dataRowStyle);

                // SEARCHED_CHEMICAL
                cell = row.createCell(0);
                cell.setCellValue(details.getPreferredName());

                cell = row.createCell(1);
                cell.setCellValue(toxvalBatchSearch.getDtxsid());

                cell = row.createCell(2);
                cell.setCellValue(toxvalBatchSearch.getCasrn());

                cell = row.createCell(3);
                cell.setCellValue(toxvalBatchSearch.getName());

                cell = row.createCell(4);
                cell.setCellValue(toxvalBatchSearch.getSource());

                cell = row.createCell(5);
                cell.setCellValue(toxvalBatchSearch.getSubsource());

                cell = row.createCell(6);
                cell.setCellValue(toxvalBatchSearch.getToxvalType());

                cell = row.createCell(7);
                cell.setCellValue(toxvalBatchSearch.getToxvalSubtype());

                cell = row.createCell(8);
                cell.setCellValue(toxvalBatchSearch.getToxvalTypeSupercategory());

                cell = row.createCell(9);
                cell.setCellValue(toxvalBatchSearch.getQualifier());

                cell = row.createCell(10);
                cell.setCellValue(toxvalBatchSearch.getToxvalNumeric());

                cell = row.createCell(11);
                cell.setCellValue(toxvalBatchSearch.getToxvalUnits());

                cell = row.createCell(12);
                cell.setCellValue(toxvalBatchSearch.getRiskAssessmentClass());

                cell = row.createCell(13);
                cell.setCellValue(toxvalBatchSearch.getStudyType());

                cell = row.createCell(14);
                cell.setCellValue(toxvalBatchSearch.getStudyDurationClass());

                cell = row.createCell(15);
                cell.setCellValue(toxvalBatchSearch.getStudyDurationValue());

                cell = row.createCell(16);
                cell.setCellValue(toxvalBatchSearch.getStudyDurationUnits());

                cell = row.createCell(17);
                cell.setCellValue(toxvalBatchSearch.getSpeciesCommon());

                cell = row.createCell(18);
                cell.setCellValue(toxvalBatchSearch.getStrain());

                cell = row.createCell(19);
                cell.setCellValue(toxvalBatchSearch.getLatinName());

                cell = row.createCell(20);
                cell.setCellValue(toxvalBatchSearch.getSpeciesSupercategory());

                cell = row.createCell(21);
                cell.setCellValue(toxvalBatchSearch.getSex());

                cell = row.createCell(22);
                cell.setCellValue(toxvalBatchSearch.getGeneration());

                cell = row.createCell(23);
                cell.setCellValue(toxvalBatchSearch.getLifestage());

                cell = row.createCell(24);
                cell.setCellValue(toxvalBatchSearch.getExposureRoute());

                cell = row.createCell(25);
                cell.setCellValue(toxvalBatchSearch.getExposureMethod());

                cell = row.createCell(26);
                cell.setCellValue(toxvalBatchSearch.getExposureForm());

                cell = row.createCell(27);
                cell.setCellValue(toxvalBatchSearch.getMedia());

                cell = row.createCell(28);
                cell.setCellValue(toxvalBatchSearch.getEffect());

                cell = row.createCell(29);
                cell.setCellValue(toxvalBatchSearch.getExperimentalRecord());

                cell = row.createCell(30);
                cell.setCellValue(toxvalBatchSearch.getStudyGroup());

                cell = row.createCell(31);
                cell.setCellValue(toxvalBatchSearch.getLongRef());

                cell = row.createCell(32);
                cell.setCellValue(toxvalBatchSearch.getDoi());

                cell = row.createCell(33);
                cell.setCellValue(toxvalBatchSearch.getTitle());

                cell = row.createCell(34);
                cell.setCellValue(toxvalBatchSearch.getAuthor());

                cell = row.createCell(35);
                cell.setCellValue(toxvalBatchSearch.getYear());

                cell = row.createCell(36);
                cell.setCellValue(toxvalBatchSearch.getGuideline());

                cell = row.createCell(37);
                cell.setCellValue(toxvalBatchSearch.getQuality());

                cell = row.createCell(38);
                cell.setCellValue(toxvalBatchSearch.getQcCategory());

                cell = row.createCell(39);
                cell.setCellValue(toxvalBatchSearch.getSourceHash());

                cell = row.createCell(40);
                cell.setCellValue(toxvalBatchSearch.getExternalSourceId());

                cell = row.createCell(41);
                cell.setCellValue(toxvalBatchSearch.getSourceUrl());

                cell = row.createCell(42);
                cell.setCellValue(toxvalBatchSearch.getSubsourceUrl());

                cell = row.createCell(43);
                cell.setCellValue(toxvalBatchSearch.getStoredSourceRecord());

                cell = row.createCell(44);
                cell.setCellValue(toxvalBatchSearch.getToxvalTypeOriginal());

                cell = row.createCell(45);
                cell.setCellValue(toxvalBatchSearch.getToxvalSubtypeOriginal());

                cell = row.createCell(46);
                cell.setCellValue(toxvalBatchSearch.getToxvalNumericOriginal());

                cell = row.createCell(47);
                cell.setCellValue(toxvalBatchSearch.getToxvalUnitsOriginal());

                cell = row.createCell(48);
                cell.setCellValue(toxvalBatchSearch.getStudyTypeOriginal());

                cell = row.createCell(49);
                cell.setCellValue(toxvalBatchSearch.getStudyDurationClassOriginal());

                cell = row.createCell(50);
                cell.setCellValue(toxvalBatchSearch.getStudyDurationValueOriginal());

                cell = row.createCell(51);
                cell.setCellValue(toxvalBatchSearch.getStudyDurationUnitsOriginal());

                cell = row.createCell(52);
                cell.setCellValue(toxvalBatchSearch.getSpeciesOriginal());

                cell = row.createCell(53);
                cell.setCellValue(toxvalBatchSearch.getStrainOriginal());

                cell = row.createCell(54);
                cell.setCellValue(toxvalBatchSearch.getSexOriginal());

                cell = row.createCell(55);
                cell.setCellValue(toxvalBatchSearch.getGenerationOriginal());

                cell = row.createCell(56);
                cell.setCellValue(toxvalBatchSearch.getLifestageOriginal());

                cell = row.createCell(57);
                cell.setCellValue(toxvalBatchSearch.getExposureRouteOriginal());

                cell = row.createCell(58);
                cell.setCellValue(toxvalBatchSearch.getExposureMethodOriginal());

                cell = row.createCell(59);
                cell.setCellValue(toxvalBatchSearch.getExposureFormOriginal());

                cell = row.createCell(60);
                cell.setCellValue(toxvalBatchSearch.getMediaOriginal());

                cell = row.createCell(61);
                cell.setCellValue(toxvalBatchSearch.getEffectOriginal());

                cell = row.createCell(62);
                cell.setCellValue(toxvalBatchSearch.getOriginalYear());
            }
        }
    }

    protected void updateChemicalPropertiesSheet(List<ChemicalProperties> chemicalProperties) {
        log.debug("updating chemical properties sheet ");

        //CellStyle dataRowStyle = getNormalStyle();

        Sheet sheet = workbook.getSheet(CHEMICAL_PROPERTIES_DETAILS);
        //int currentRow = 1;
        Integer currentRow = sheetCurrentRow.get(CHEMICAL_PROPERTIES_DETAILS);
        Cell cell;

        for (ChemicalProperties properties : chemicalProperties){
            Row row = sheet.createRow(currentRow++);
            row.setRowStyle(dataRowStyle);

            // DTXSID
            cell = row.createCell(0);
            cell.setCellValue(properties.getDtxsid());

            // DTXCID
            cell = row.createCell(1);
            cell.setCellValue(properties.getDtxcid());

            // TYPE
            cell = row.createCell(2);
            cell.setCellValue(properties.getPropType());

            // NAME
            cell = row.createCell(3);
            cell.setCellValue(properties.getName());

            // VALUE
            cell = row.createCell(4);
            cell.setCellValue(checkNull(properties.getValue()));

            // UNIT
            cell = row.createCell(5);
            cell.setCellValue(properties.getUnit());

            // SOURCE
            cell = row.createCell(6);
            cell.setCellValue(properties.getSource());

            // DESCRIPTION
            cell = row.createCell(7);
            cell.setCellValue(properties.getDescription());
        }
    }

    private void updateMainDataSheet(String searchWord, SearchWithChemicalDetails details, String tab) {
        log.debug("search word={}, dtxsid={}, tb={}", searchWord, details.getDtxsid(), tab);

        List<String> mainDataSheetHeaders = headers.get(tab);

        Sheet sheet = workbook.getSheet(tab);
        Integer currentRow = sheetCurrentRow.get(tab);

        Row row = sheet.createRow(currentRow++);
        sheetCurrentRow.put(tab, currentRow);
        row.setRowStyle(dataRowStyle);

        int colIndex = 0;

        for (String column : mainDataSheetHeaders) {
            Cell cell = row.createCell(colIndex++);
            String cellValue = getColumnContents(searchWord, column, details);

            if(cellValue == null)
                log.error("search word={} column={} tab={} dtxsid={} ", searchWord, column, details.getDtxsid());

            // no column found, cellValue should be the column name
            String toxPrint = getColumnContents(searchWord, "TOXPRINTS_CHEMOTYPER", details);
            String[] tpArray = toxPrint.split("\t");

            log.debug("tpArray length = {}, toxprintHeader size={}", tpArray.length, toxprintHeader.size());
            assert cellValue != null;
            if (cellValue.equalsIgnoreCase(column)) {
                if ((chemlistLookup == null || !chemlistLookup.contains(column)) && toxprintHeader.contains(column)) {
                    if(toxprintHeader.size() == tpArray.length) {

                                int index = toxprintHeader.indexOf(column);
                                cellValue = tpArray[index];
                            }

                    //if tpArray is empty,cellValue will be empty
                    else {
                        cellValue = null;
                    }
                    // look for matching chemical name
                    // lookup.add(chemlist.getDtxsid() + "-" + chemlist.getListName());
                } else if(chemlistLookup != null && chemlistLookup.contains(details.getDtxsid() + "-" + column))
                    cellValue = "Y";
                else
                    cellValue = "-";
            }
            if(NumberUtils.isCreatable(cellValue)){
                //log.debug("cell value {} is numeric", cellValue);
                cell.setCellStyle(numericDataStyle);
                cell.setCellValue(Double.parseDouble(cellValue));
            }else{
                //log.debug("cell value {} is String", cellValue);
                cell.setCellValue(cellValue);
            }

            if(hyperLinkColumns.contains(column) && !cellValue.equals(EMPTY_VALUE)){
                cell.setHyperlink(getHyperLink(column, details.getDtxsid(), details.getDtxcid(),
                        details.getWikipediaArticle()));
                cell.setCellStyle(hyperLinkStyle); // add blue color to link text
            }
        }
    }

    //
    public void updateCoverSheet(BatchSearchForm searchForm, int foundCount, int notFoundCount, int duplicates){

//        coverSheetStyle.setWrapText(true);

        Sheet sheet = workbook.getSheet(COVER_SHEET);

        // set column width
        sheet.setColumnWidth(0, 25 * 256);
        sheet.setColumnWidth(1, 100 * 256);
        int currentRow = 1;

        Row row;
        Cell cell;
        int firstColumn=0;
        int secondColumn=1;

        // Add warning row
        row = sheet.createRow(currentRow++);
        row.setRowStyle(dataRowStyle);
        row.setHeight((short) (3 * 256));

        // row.setHeight((short)-1);

        if (duplicates>0) {
            cell = row.createCell(firstColumn);
            cell.setCellValue("WARNING");
            cell.setCellStyle(headerStyle);

            cell = row.createCell(secondColumn);
            cell.setCellValue("DO NOT COPY / PASTE THIS DATA \n Some search terms returned multiple values, copy/paste will result in misaligned data");
        }

        // Extracted_date
        row = sheet.createRow(currentRow++);
        row.setRowStyle(dataRowStyle);

        cell = row.createCell(firstColumn);
        cell.setCellValue("Search datestamp");

        cell = row.createCell(secondColumn);
        cell.setCellValue(sdf3.format(new Timestamp(System.currentTimeMillis())));

        // Search_terms
        row = sheet.createRow(currentRow++);
        row.setRowStyle(dataRowStyle);

        cell = row.createCell(firstColumn);
        cell.setCellValue("Search term count");

        cell = row.createCell(secondColumn);
        cell.setCellValue(searchForm.getSearchItems().split("\n").length);

        // match count
        row = sheet.createRow(currentRow++);
        row.setRowStyle(dataRowStyle);

        cell = row.createCell(firstColumn);
        cell.setCellValue("Found count");

        cell = row.createCell(secondColumn);
        cell.setCellValue(foundCount);

        // not match count
        row = sheet.createRow(currentRow++);
        row.setRowStyle(dataRowStyle);

        cell = row.createCell(firstColumn);
        cell.setCellValue("Not found count");

        cell = row.createCell(secondColumn);
        cell.setCellValue(notFoundCount);

        // duplicates
        row = sheet.createRow(currentRow++);
        row.setRowStyle(dataRowStyle);

        cell = row.createCell(firstColumn);
        cell.setCellValue("Duplicate count");

        cell = row.createCell(secondColumn);
        cell.setCellValue(duplicates);

        //
//        sheet.autoSizeColumn(1);
//        sheet.autoSizeColumn(2);
    }

    // This sheet is different from other sheets. Here columns has search words
    protected void updateToxcastSheet(List<String> assaysList) {

        log.debug("updating toxcast sheet ");

        // CellStyle dataRowStyle = getNormalStyle();

        Sheet sheet = workbook.getSheet(ASSOCIATED_TOXCAST_ASSAYS);

        int currentRow = 1; // skipping header row
        int currentColumn = 0;
        Cell cell;

        CellStyle blueBg = sheet.getWorkbook().createCellStyle();
        blueBg.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle redBg = sheet.getWorkbook().createCellStyle();
        redBg.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for(String assay : assaysList){
            currentColumn = 0;
            Row row = sheet.createRow(currentRow++);
            row.setRowStyle(dataRowStyle);
            cell = row.createCell(currentColumn++);
            cell.setCellValue(assay); // fill first column with assay names

            //
            log.debug("assay = {}, values={}", assay, toxcastLookup.get(assay));
            for(String val : toxcastLookup.get(assay)){
                cell = row.createCell(currentColumn++);
                cell.setCellValue(val);
                if(val.equals("0")){
                    cell.setCellStyle(blueBg);
                }else if(val.equals("1")){
                    cell.setCellStyle(redBg);
                }
            }
        }
    }

    //
    private Hyperlink getHyperLink(String column, String dtxsid, String dtxcid, String wikipediaArticle) {
        final Hyperlink href =  workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
        switch (column)
        {
            case "EXPOCAST":
                href.setAddress("https://comptox.epa.gov/dashboard/chemical/exposure-predictions/" + dtxsid);
                break;
            case "NHANES":
                href.setAddress("https://comptox.epa.gov/dashboard/chemical/monitoring-data/" + dtxsid);
                break;
            case "TOXVAL_DATA":
                href.setAddress("https://comptox.epa.gov/dashboard/chemical/executive-summary/" + dtxsid);
                break;
            case "DSSTOX_LINK_TO_DASHBOARD":
            case "DTXSID":
                href.setAddress("https://comptox.epa.gov/dashboard/chemical/details/" + dtxsid);
                break;
            case "DTXCID":
                href.setAddress("https://comptox.epa.gov/dashboard/chemical/details/" + dtxcid);
                break;
            case "IRIS_LINK":
                href.setAddress("https://comptox.epa.gov/dashboard/chemical/literature-iris/" + dtxsid)
                ;break;
            case "PPRTV_LINK":
                href.setAddress("https://comptox.epa.gov/dashboard/chemical/literature-pprtv/" + dtxsid);
                break;
            case "WIKIPEDIA_ARTICLE":
                href.setAddress(UriUtils.encodePath("https://en.wikipedia.org/wiki/" + wikipediaArticle, "UTF-8"));
                break;
            case "SAFETY_DATA":
                    href.setAddress(SearchChemicalService.getSafetyUrl(dtxsid));
        }
        return href;
    }

    protected String getActorWsUrl(String casrn) {
        if(StringUtils.isNotBlank(casrn))
            return "https://actorws.epa.gov/actorws/actor/2015q3/chemicalPdfExport.pdf?casrn=" + casrn;
        else
            return EMPTY_VALUE;
    }

    protected Set<String> buildChemicalListlookup(List<ChemlistToSubtance> chemlistToSubtanceList){
        Set<String> lookup = new HashSet<>();

        log.debug("build chemical list lookup - count()={} ", chemlistToSubtanceList.size());
        for(ChemlistToSubtance chemlist : chemlistToSubtanceList){
            lookup.add(chemlist.getDtxsid() + "-" + chemlist.getListName());
        }

        return lookup;
    }

    protected Hashtable<String, ChemicalSynonym> buildChemicalSynonymslookup(List<ChemicalSynonym> chemicalSynonyms) {
        Hashtable<String, ChemicalSynonym> synonymHashtable = new Hashtable<>();

        log.debug("build synonym lookup - count()={} ", chemicalSynonyms.size());
        for(ChemicalSynonym synonym : chemicalSynonyms){
            synonymHashtable.put(synonym.getDtxsid(), synonym);
        }
        return synonymHashtable;
    }

    protected Hashtable<String, List<ToxvalBatchSearch>>  buildToxvalBatchSearchlookup(List<ToxvalBatchSearch> toxvalBatchSearches) {
        Hashtable<String, List<ToxvalBatchSearch>> toxvalHashtable = new Hashtable<>();

        log.debug("build toxval batchsearch lookup - count()={} ", toxvalBatchSearches.size());

        String oldDtxsid = "";
        List<ToxvalBatchSearch> toxvals = new ArrayList<>();

        for(ToxvalBatchSearch tb : toxvalBatchSearches){
            if(oldDtxsid.equals("")){
                oldDtxsid = tb.getDtxsid();
                toxvals.add(tb);
            }else if (oldDtxsid.equals(tb.getDtxsid())){
                toxvals.add(tb);
            }else{
                toxvalHashtable.put(oldDtxsid, toxvals);
                toxvals = new ArrayList<>();
                oldDtxsid = tb.getDtxsid();
                toxvals.add(tb);
            }
        }

        toxvalHashtable.put(oldDtxsid, toxvals); // last record

        return toxvalHashtable;
    }

    protected Hashtable<String, List<ToxrefBatchSearch>>  buildToxrefBatchSearchlookup(List<ToxrefBatchSearch> toxrefBatchSearches) {
        Hashtable<String, List<ToxrefBatchSearch>> toxrefHashtable = new Hashtable<>();

        log.debug("build toxref batchsearch lookup - count()={} ", toxrefBatchSearches.size());

        String oldDtxsid = "";
        List<ToxrefBatchSearch> toxvals = new ArrayList<>();

        for(ToxrefBatchSearch tb : toxrefBatchSearches){
            if(oldDtxsid.equals("")){
                oldDtxsid = tb.getDtxsid();
                toxvals.add(tb);
            }else if (oldDtxsid.equals(tb.getDtxsid())){
                toxvals.add(tb);
            }else{
                toxrefHashtable.put(oldDtxsid, toxvals);
                toxvals = new ArrayList<>();
                oldDtxsid = tb.getDtxsid();
                toxvals.add(tb);
            }
        }

        toxrefHashtable.put(oldDtxsid, toxvals); // last record

        return toxrefHashtable;
    }

    protected Hashtable<String, List<RelatedSubstance>> buildRelatedSublookup(List<RelatedSubstance> relatedSubstances) {
        Hashtable<String, List<RelatedSubstance>> relatedSubHashTable = new Hashtable<>();

        log.debug("build related substances lookup - count()={} ", relatedSubstances.size());

        String oldId = "";
        List<RelatedSubstance> related = new ArrayList<>();
        for(RelatedSubstance substance : relatedSubstances){
            if(oldId.equals("")){
                oldId = substance.getDtxsid();
                related.add(substance);
            }else if(oldId.equals(substance.getDtxsid())){
                related.add(substance);
            }else{
                relatedSubHashTable.put(oldId, related);
                related = new ArrayList<>();
                oldId = substance.getDtxsid();
                related.add(substance);
            }
        }
        relatedSubHashTable.put(oldId, related); // last record

        return relatedSubHashTable;
    }

    protected void buildToxcastLookup(HashMap<String, List<SearchWithChemicalDetails>> results,
                                      List<BioactivityAssayList> assayResults,
                                      List<String> assaysList){

        toxcastLookup = new Hashtable<>();
        assayColumns = new ArrayList<>();

        Hashtable<String, String> bioLookup = new Hashtable<>();

        log.debug("assay results size = {}", assayResults.size());
        for(BioactivityAssayList bio : assayResults){
            String key = bio.getAssayComponentEndpointName()+ "_"+bio.getDtxsid();
            log.debug("key = {}", key);
            bioLookup.put(key, bio.getAc50().toString());
        }

        for (String assay : assaysList){
            List<String> toxcastData = new ArrayList<>();
            results.forEach((s, searchWithChemicalDetails) -> {
                for (SearchWithChemicalDetails details : searchWithChemicalDetails) {
                    String key = assay + "_" + details.getDtxsid();
                    if(bioLookup.containsKey(key)){
                        log.debug("key={}, value={}", key, bioLookup.get(key));
                        toxcastData.add(bioLookup.get(key));
                        //toxcastLookup.put(key + "_" + searchWord, );
                    }else{
                        toxcastData.add("-");
                        //toxcastLookup.put(key + "_" + searchWord, "-");
                    }
                }
            });
            log.debug("assay={},values={}", assay, toxcastData);
            toxcastLookup.put(assay, toxcastData);
        }

        for(String searchWord : results.keySet()){
            List<SearchWithChemicalDetails> detailsList = results.get(searchWord);
            for(SearchWithChemicalDetails details : detailsList){
                String columnName = searchWord+"_"+details.getDtxsid();
                assayColumns.add(columnName); // for building columns
            }
        }
    }

}
