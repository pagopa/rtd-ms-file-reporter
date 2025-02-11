package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "report")
public record ReportFileTTL
        (@NotNull
        @NotBlank
        String fileTTL) {

}
