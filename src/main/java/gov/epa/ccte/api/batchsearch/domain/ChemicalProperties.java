package gov.epa.ccte.api.batchsearch.domain;

import lombok.*;
import jakarta.persistence.*;

/**
 * 
 * @author arashid
 * Create at 2022-06-06 14:23
 */
@Entity
@Table(name = "chemical_properties")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChemicalProperties {

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
    @Column(name = "dtxcid", length = 45)
    private String dtxcid;

    /**
     * 
     */
    @Column(name = "prop_type")
    private String propType;

    /**
     * 
     */
    @Column(name = "unit")
    private String unit;

    /**
     * 
     */
    @Column(name = "name")
    private String name;

    /**
     * 
     */
    @Column(name = "value")
    private Double value;

    /**
     * 
     */
    @Column(name = "source")
    private String source;

    /**
     * 
     */
    @Column(name = "description", length = 1024)
    private String description;
}
