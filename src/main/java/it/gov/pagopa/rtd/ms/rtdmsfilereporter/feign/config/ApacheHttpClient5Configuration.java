package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.apachehttpclient.v5_2.ApacheHttpClient5Telemetry;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.context.annotation.Bean;

public class ApacheHttpClient5Configuration {

  private OpenTelemetry openTelemetry;

  // creates a new http client builder for constructing http clients with open telemetry instrumentation
  @Bean
  public HttpClientBuilder createBuilder() {
    return ApacheHttpClient5Telemetry.builder(openTelemetry).build().newHttpClientBuilder();
  }

  // creates a new http client with open telemetry instrumentation
  @Bean
  public HttpClient newHttpClient() {
    return ApacheHttpClient5Telemetry.builder(openTelemetry).build().newHttpClient();
  }
}
