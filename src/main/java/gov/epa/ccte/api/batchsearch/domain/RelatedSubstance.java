package gov.epa.ccte.api.batchsearch.domain;import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.UUID;

/**
 * 
 * @author arashid
 * Create at 2021-06-16 14:32
 */
@Entity
@Table(name = "related_substance")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RelatedSubstance {

    /**
     *
     */
    @Id
    @Column(name = "id")
    private BigInteger id;


    /**
     * 
     */
    @Column(name = "dtxsid", length = 45)
    private String dtxsid;

    /**
     * 
     */
    @Column(name = "preferred_name", length = 1)
    private String preferredName;

    /**
     * 
     */
    @Column(name = "realted_dtxsid", length = 45)
    private String realtedDtxsid;

    /**
     * 
     */
    @Column(name = "related_casrn")
    private String relatedCasrn;

    /**
     * 
     */
    @Column(name = "related_preferred_name", length = 1)
    private String relatedPreferredName;

    /**
     * 
     */
    @Column(name = "relationship", length = 1)
    private String relationship;
}
