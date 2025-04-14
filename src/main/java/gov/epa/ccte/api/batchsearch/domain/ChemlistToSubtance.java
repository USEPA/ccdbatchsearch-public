package gov.epa.ccte.api.batchsearch.domain;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

/**
 * 
 * @author arashid
 * Create at 2021-06-08 13:51
 */
@Entity
@Table(name = "chemlist_to_subtance")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChemlistToSubtance {

    /**
     * 
     */
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    /**
     * 
     */
    @Column(name = "dtxsid", length = 20)
    private String dtxsid;

    /**
     * 
     */
    @Column(name = "list_name", length = 50)
    private String listName;

    /**
     * 
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 
     */
    @Column(name = "create_timestamp")
    private OffsetDateTime createTimestamp;

    /**
     * 
     */
    @Column(name = "update_timestamp")
    private OffsetDateTime updateTimestamp;
}
