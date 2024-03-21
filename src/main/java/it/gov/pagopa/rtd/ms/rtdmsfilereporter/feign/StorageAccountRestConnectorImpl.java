package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.StorageProperties;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class StorageAccountRestConnectorImpl {

  private final StorageProperties properties;
  private final CloseableHttpClient httpClient;

  public Header[] getBlobMetadata(String basePath, String fileName) throws IOException {

    var uri = properties.url()
        + basePath
        + fileName
        + "?comp=metadata";

    final HttpGet httpGet = new HttpGet(uri);
    httpGet.setHeader(new BasicHeader("Ocp-Apim-Subscription-Key", properties.apiKey()));

    return httpClient.execute(httpGet, response -> {
      log.info("response is {}", response.getReasonPhrase());
      return response.getHeaders();
    });
  }
}
