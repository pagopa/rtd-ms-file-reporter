package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;

@RequiredArgsConstructor
public class FeignClientConfig {

  private final StorageProperties properties;

  @Bean
  public RequestInterceptor requestInterceptor() {
    // This interceptor injects the api key header in each request made with the client.
    return requestTemplate -> requestTemplate.header(
        "Ocp-Apim-Subscription-Key", properties.apiKey());
  }
}
