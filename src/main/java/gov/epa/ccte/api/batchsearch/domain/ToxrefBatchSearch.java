
package gov.epa.ccte.api.batchsearch.domain;

import lombok.*;
import jakarta.persistence.*;


@Entity
@Table(name = "toxref_batch_search")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ToxrefBatchSearch {

    @Id
    @Column(name = "tbs_key")
    private String tbsKey;

    @Column(name = "dsstox_substance_id")
    private String dtxsid;

    @Column(name = "study_id")
    private String studyId;


    @Column(name = "casrn")
    private String casrn;

    @Column(name = "name")
    private String name;

    @Column(name = "study_source")
    private String studySource;

    @Column(name = "study_source_id")
    private String studySourceId;

    @Column(name = "citation")
    private String citation;

    @Column(name = "study_year")
    private String studyYear;

    @Column(name = "study_type")
    private String studyType;

    @Column(name = "study_type_guideline")
    private String studyTypeGuideline;

    @Column(name = "species")
    private String species;

    @Column(name = "strain_group")
    private String strainGroup;

    @Column(name = "strain")
    private String strain;

    @Column(name = "admin_route")
    private String adminRoute;

    @Column(name = "admin_method")
    private String adminMethod;

    @Column(name = "dose_duration")
    private String doseDuration;

    @Column(name = "dose_duration_unit")
    private String doseDurationUnit;

    @Column(name = "dose_start")
    private Integer doseStart;

    @Column(name = "dose_start_unit")
    private String doseStartUnit;

    @Column(name = "dose_end")
    private Integer doseEnd;

    @Column(name = "dose_end_unit")
    private String doseEndUnit;

    @Column(name = "dose_period")
    private String dosePeriod;

    @Column(name = "dose_level")
    private Integer doseLevel;

    @Column(name = "conc")
    private String conc;

    @Column(name = "conc_unit")
    private String concUnit;

    @Column(name = "vehicle")
    private String vehicle;

    @Column(name = "dose_comment")
    private String doseComment;

    @Column(name = "dose_adjusted")
    private String doseAdjusted;

    @Column(name = "dose_adjusted_unit")
    private String doseAdjustedUnit;

    @Column(name = "sex")
    private String sex;

    @Column(name = "generation")
    private String generation;

    @Column(name = "life_stage")
    private String lifeStage;

    @Column(name = "num_animals")
    private String numAnimals;

    @Column(name = "tg_comment")
    private String tgComment;

    @Column(name = "endpoint_category")
    private String endpointCategory;

    @Column(name = "endpoint_type")
    private String endpointType;

    @Column(name = "endpoint_target")
    private String endpointTarget;

    @Column(name = "effect_desc")
    private String effectDesc;

    @Column(name = "effect_desc_free")
    private String effectDescFree;

    @Column(name = "cancer_related")
    private Boolean cancerRelated;

    @Column(name = "target_site")
    private String targetSite;

    @Column(name = "direction")
    private String direction;

    @Column(name = "effect_comment")
    private String effectComment;

    @Column(name = "treatment_related")
    private Boolean treatmentRelated;

    @Column(name = "critical_effect")
    private Boolean criticalEffect;

    @Column(name = "sample_size")
    private String sampleSize;

    @Column(name = "effect_val")
    private String effectVal;

    @Column(name = "effect_val_unit")
    private String effectValUnit;

    @Column(name = "effect_var")
    private String effectVar;

    @Column(name = "effect_var_type")
    private String effectVarType;

    @Column(name = "time")
    private String time;

    @Column(name = "time_unit")
    private String timeUnit;

    @Column(name = "no_quant_data_reported")
    private Boolean noQuantDataReported;


}
