package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.StorageProperties;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class StorageAccountRestConnector {

  public static final String BLOB_METADATA_PREFIX = "x-ms-meta-";
  public static final String BLOB_METADATA_QUERY = "?comp=metadata";
  public static final String SUBSCRIPTION_KEY_HEADER = "Ocp-Apim-Subscription-Key";
  private final StorageProperties properties;
  private final CloseableHttpClient httpClient;

  public Map<String, String> getBlobMetadata(String basePath, String fileName) throws IOException {

    var uri = properties.url()
        + basePath
        + fileName
        + BLOB_METADATA_QUERY;

    final HttpGet httpGet = new HttpGet(uri);
    httpGet.setHeader(new BasicHeader(SUBSCRIPTION_KEY_HEADER, properties.apiKey()));

    return httpClient.execute(httpGet, validateAndGetMetadata());
  }

  /**
   * Returns the metadata as a Map. Contains the logic about where to retrieve the metadata in the
   * response entity (e.g. in the body, in the headers etc...) and cleanup the data.
   *
   * @return a map containing the metadata
   */
  protected HttpClientResponseHandler<Map<String, String>> validateAndGetMetadata() {
    return response -> {
      if (200 != response.getCode()) {
        throw new IOException(response.getReasonPhrase());
      }

      return Arrays.stream(response.getHeaders())
          .filter(header -> header.getName().startsWith(BLOB_METADATA_PREFIX))
          .map(this::removeAdditionalHeaderText)
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    };
  }

  @NotNull
  private Entry<String, String> removeAdditionalHeaderText(Header header) {
    return Map.entry(header.getName().replaceFirst(BLOB_METADATA_PREFIX, ""),
        header.getValue());
  }
}
