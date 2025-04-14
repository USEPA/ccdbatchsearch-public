package gov.epa.ccte.api.batchsearch.domain;
import lombok.*;
import jakarta.persistence.*;

/**
 * 
 * @author arashid
 * Create at 2022-06-02 11:54
 */
@Entity
@Table(name = "toxval_batch_search")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToxvalBatchSearch {
    /**
     *
     */
    @Id
    @Setter(AccessLevel.PROTECTED)
    @NonNull
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 
     */
    @Column(name = "dtxsid", length = 45)
    private String dtxsid;

    /**
     * 
     */
    @Column(name = "casrn")
    private String casrn;

    /**
     * 
     */
    @Column(name = "name")
    private String name;

    /**
     * 
     */
    @Column(name = "source")
    private String source;

    /**
     * 
     */
    @Column(name = "subsource")
    private String subsource;

    /**
     * 
     */
    @Column(name = "toxval_type")
    private String toxvalType;

    /**
     * 
     */
    @Column(name = "toxval_type_original")
    private String toxvalTypeOriginal;

    /**
     * 
     */
    @Column(name = "toxval_subtype")
    private String toxvalSubtype;

    /**
     * 
     */
    @Column(name = "toxval_type_supercategory")
    private String toxvalTypeSupercategory;



    @Column(name = "toxval_numeric_qualifier")
    private String qualifier;

    /**
     * 
     */
    @Column(name = "toxval_numeric")
    private Double toxvalNumeric;

    /**
     * 
     */
    @Column(name = "toxval_numeric_original")
    private String toxvalNumericOriginal;

    /**
     * 
     */
    @Column(name = "toxval_units")
    private String toxvalUnits;

    /**
     * 
     */
    @Column(name = "toxval_units_original")
    private String toxvalUnitsOriginal;

    /**
     * 
     */
    @Column(name = "risk_assessment_class")
    private String riskAssessmentClass;

    /**
     * 
     */
    @Column(name = "study_type")
    private String studyType;

    /**
     * 
     */
    @Column(name = "study_type_original")
    private String studyTypeOriginal;

    /**
     * 
     */
    @Column(name = "study_duration_class")
    private String studyDurationClass;

    /**
     * 
     */
    @Column(name = "study_duration_class_original")
    private String studyDurationClassOriginal;

    /**
     * 
     */
    @Column(name = "study_duration_value")
    private Double studyDurationValue;

    /**
     * 
     */
    @Column(name = "study_duration_value_original")
    private String studyDurationValueOriginal;

    /**
     * 
     */
    @Column(name = "study_duration_units")
    private String studyDurationUnits;

    /**
     * 
     */
    @Column(name = "study_duration_units_original")
    private String studyDurationUnitsOriginal;

    /**
     * 
     */
    @Column(name = "species_original")
    private String speciesOriginal;

    /**
     * 
     */
    @Column(name = "species_common")
    private String speciesCommon;

    /**
     * 
     */
    @Column(name = "species_supercategory")
    private String speciesSupercategory;

    /**
     * 
     */
    @Column(name = "strain")
    private String strain;

    /**
     * 
     */
    @Column(name = "strain_original")
    private String strainOriginal;

    /**
     * 
     */
    @Column(name = "sex")
    private String sex;

    /**
     * 
     */
    @Column(name = "sex_original")
    private String sexOriginal;

    /**
     * 
     */
    @Column(name = "generation")
    private String generation;

    /**
     * 
     */
    @Column(name = "lifestage")
    private String lifestage;

    /**
     * 
     */
    @Column(name = "exposure_route")
    private String exposureRoute;

    /**
     * 
     */
    @Column(name = "exposure_route_original")
    private String exposureRouteOriginal;

    /**
     * 
     */
    @Column(name = "exposure_method")
    private String exposureMethod;

    /**
     * 
     */
    @Column(name = "exposure_method_original")
    private String exposureMethodOriginal;

    /**
     * 
     */
    @Column(name = "exposure_form")
    private String exposureForm;

    /**
     * 
     */
    @Column(name = "exposure_form_original")
    private String exposureFormOriginal;

    /**
     * 
     */
    @Column(name = "media")
    private String media;

    /**
     * 
     */
    @Column(name = "media_original")
    private String mediaOriginal;

    /**
     * 
     */
    @Column(name = "effect", length = 1024)
    private String effect;

    /**
     * 
     */
    @Column(name = "effect_original", length = 1024)
    private String effectOriginal;

    /**
     * 
     */
    @Column(name = "original_year")
    private String originalYear;

    /**
     * 
     */
    @Column(name = "long_ref")
    private String longRef;

    /**
     * 
     */
    @Column(name = "title")
    private String title;

    /**
     * 
     */
    @Column(name = "author")
    private String author;

    /**
     * 
     */
    @Column(name = "year", length = 45)
    private String year;


    /**
     * 
     */
    @Column(name = "guideline")
    private String guideline;

    /**
     * 
     */
    @Column(name = "quality")
    private String quality;


    @Column(name = "qc_category")
    private String qcCategory;

    @Column(name = "experimental_record")
    private String experimentalRecord;

    @Column(name = "study_group")
    private String studyGroup;

    @Column(name = "latin_name")
    private String latinName;

    @Column(name = "lifestage_original")
    private String lifestageOriginal;

    @Column(name = "source_hash")
    private String sourceHash;

    @Column(name = "external_source_id")
    private String externalSourceId;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "subsource_url")
    private String subsourceUrl;

    @Column(name = "toxval_subtype_original")
    private String toxvalSubtypeOriginal;


    @Column(name = "stored_source_record")
    private String storedSourceRecord;

    @Column(name = "doi")
    private String doi;

    @Column(name = "generation_original")
    private String generationOriginal;

}