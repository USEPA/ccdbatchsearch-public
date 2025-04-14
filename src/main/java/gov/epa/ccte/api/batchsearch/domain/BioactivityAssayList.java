package gov.epa.ccte.api.batchsearch.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

/**
 *
 * @author arashid
 * Create at 2021-06-21 11:34
 */
@Entity
@Table(name = "bioactivity_assay_list")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BioactivityAssayList {

    /**
     *
     */
    @Id
    @Column(name = "id")
    private Long id;

    /**
     *
     */
    @Column(name = "dtxsid", length = 45)
    private String dtxsid;

    /**
     *
     */
    @Column(name = "assay_component_endpoint_name")
    private String assayComponentEndpointName;

    /**
     *
     */
//    @Column(name = "name", length = 1)
//    private String name;

    /**
     *
     */
//    @Column(name = "description", length = 1)
//    private String description;

    /**
     *
     */
    @Column(name = "ac50")
    // private BigDecimal hitc;
    private Double ac50;

    /**
     *
     */
//    @Column(name = "chid_rep", precision = 1)
//    private BigDecimal chidRep;

    /**
     *
     */
//    @Column(name = "intended_target", length = 50)
//    private String intendedTarget;

    /**
     *
     */
//    @Column(name = "cell_line")
//    private String cellLine;

    /**
     *
     */
//    @Column(name = "cell_format")
//    private String cellFormat;

    /**
     *
     */
//    @Column(name = "detection_technology", length = 200)
//    private String detectionTechnology;

    /**
     *
     */
//    @Column(name = "m4id")
//    private Long m4id;

    /**
     *
     */
//    @Column(name = "gene")
//    private Object gene;
}
