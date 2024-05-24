package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.StorageProperties;
import java.io.IOException;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.impl.io.DefaultClassicHttpResponseFactory;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageAccountRestConnectorTest {

  StorageAccountRestConnector connector;
  @Mock
  HttpClient client;

  @BeforeEach
  void setUp() {
    connector = new StorageAccountRestConnector(new StorageProperties("/url/", "key"));
  }

  @Test
  @SneakyThrows
  void whenGetBlobTheReturnMap() {
    var basePath = "/container/";
    var name = "name";
    var httpGet = new HttpGet(basePath + name + StorageAccountRestConnector.BLOB_METADATA_QUERY);
    httpGet.setHeaders(new BasicHeader("Ocp-Apim-Subscription-Key", "key"));
    when(client.execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class)))
        .thenReturn(Map.of());

    var response = connector.getBlobMetadata(basePath, name);

    verify(client).execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class));
    assertThat(response).isNotNull().isEmpty();
  }

  @Test
  @SneakyThrows
  void givenResponseWith404WhenClientExecuteThenThrowException() {
    when(client.execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class)))
        .thenThrow(new IOException("KO"));

    assertThatThrownBy(() -> connector.getBlobMetadata("/basePath/", "name"))
        .isInstanceOf(IOException.class)
        .hasMessage("KO");

    verify(client).execute(any(ClassicHttpRequest.class), any(HttpClientResponseHandler.class));
  }

  @SneakyThrows
  @ParameterizedTest
  @ValueSource(strings = {"missingBothSlash", "/missingFinalSlash", "missingInitialSlash/"})
  void givenABadPathFormatWhenGetMetadataThenThrowException(String basePath) {

    assertThatThrownBy(() -> connector.getBlobMetadata(basePath, "name"))
        .isInstanceOf(IllegalArgumentException.class);

    verify(client, times(0)).execute(any(ClassicHttpRequest.class),
        any(HttpClientResponseHandler.class));
  }

  @Test
  @SneakyThrows
  void givenResponseWith404WhenValidateThenThrowException() {
    var response = DefaultClassicHttpResponseFactory.INSTANCE.newHttpResponse(404, "not found");
    response.setHeaders(new BasicHeader("content-length", 10),
        new BasicHeader(StorageAccountRestConnector.BLOB_METADATA_PREFIX + "foo", "bar"),
        new BasicHeader(StorageAccountRestConnector.BLOB_METADATA_PREFIX + "key", "value"));

    var lambda = connector.validateAndGetMetadata();

    assertThatThrownBy(() -> lambda.handleResponse(response))
        .isInstanceOf(IOException.class);
  }

  @Test
  @SneakyThrows
  void givenResponseWith200WhenValidateThenReturnsMetadata() {
    var response = DefaultClassicHttpResponseFactory.INSTANCE.newHttpResponse(200, "ok");
    response.setHeaders(new BasicHeader("content-length", 10),
        new BasicHeader(StorageAccountRestConnector.BLOB_METADATA_PREFIX + "foo", "bar"),
        new BasicHeader(StorageAccountRestConnector.BLOB_METADATA_PREFIX + "key", "value"));

    var metadata = connector.validateAndGetMetadata().handleResponse(response);

    assertThat(metadata)
        .hasSize(2)
        .containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
            entry("foo", "bar"),
            entry("key", "value")));
  }

  @Test
  @SneakyThrows
  void givenResponseWith200WhenValidateThenReturnsEmptyMap() {
    var response = DefaultClassicHttpResponseFactory.INSTANCE.newHttpResponse(200, "ok");
    response.setHeaders(new BasicHeader("content-length", 10));

    var metadata = connector.validateAndGetMetadata().handleResponse(response);

    assertThat(metadata).isNotNull().isEmpty();
  }
}
