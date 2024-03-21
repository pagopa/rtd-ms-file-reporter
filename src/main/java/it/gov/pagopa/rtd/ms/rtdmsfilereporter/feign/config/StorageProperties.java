package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.cloud.openfeign.client.config.StorageAccountConnector")
public record StorageProperties
    (@NotNull
     @NotBlank
     String url,
     @NotNull
     @NotBlank
     String apiKey) {

}
