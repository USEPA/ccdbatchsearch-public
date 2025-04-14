package gov.epa.ccte.api.batchsearch.domain;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

/**
 * 
 * @author arashid
 * Create at 2021-06-16 07:36
 */
@Entity
@Table(name = "chemical_synonym")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChemicalSynonym {

    /**
     * 
     */
    @Id
    @Column(name = "dtxsid", length = 20)
    private String dtxsid;

    /**
     * 
     */
    @Column(name = "pc_code")
    private String pcCode;

    /**
     * 
     */
    @Column(name = "synonyms")
    private String synonyms;
}
