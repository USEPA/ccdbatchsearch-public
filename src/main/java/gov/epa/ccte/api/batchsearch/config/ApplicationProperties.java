package gov.epa.ccte.api.batchsearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Chemicalist.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = true)
public class ApplicationProperties {

    private String docHostReplacement;
    private int docHostPort;
    private String docHostHttpProtocol;
    private Integer startPort;
    private Integer endPort;
    private Integer serverPort;
}
