package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.StorageProperties;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicHeader;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class StorageAccountRestConnector {

  private final StorageProperties properties;
  private final CloseableHttpClient httpClient;

  public Map<String, String> getBlobMetadata(String basePath, String fileName) {

    var uri = properties.url()
        + basePath
        + fileName
        + "?comp=metadata";

    final HttpGet httpGet = new HttpGet(uri);
    httpGet.setHeader(new BasicHeader("Ocp-Apim-Subscription-Key", properties.apiKey()));

    // todo validate and return headers
    return httpClient.execute(httpGet, validateAndReturnHeaders());
  }

  private HttpClientResponseHandler<Map<String, String>> validateAndReturnHeaders() {
    return response -> {
      assert response.getCode() == 200;
      return Arrays.stream(response.getHeaders())
          .collect(Collectors.toMap(NameValuePair::getName,
              NameValuePair::getValue));
    };
  }
}
