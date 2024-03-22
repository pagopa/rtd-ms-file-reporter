package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.AggregatesDataSummaryMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.StorageAccountRestConnector;
import java.io.IOException;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorageAccountServiceTest {

  @Mock
  private StorageAccountRestConnector connector;
  private StorageAccountService service;
  @Mock
  private AggregatesDataSummaryMapper mapper;

  @BeforeEach
  void setUp() {
    service = new StorageAccountService(connector, mapper);
  }

  @Test
  @SneakyThrows
  void whenGetMetadataThenReturnsDataSummary() {

    when(connector.getBlobMetadata(any(), any()))
        .thenReturn(Map.of("data1", "value1", "key2", "value2"));
    when(mapper.getDataSummary(any()))
        .thenReturn(AggregatesDataSummary.builder()
            .sumAmountPositiveTransactions(10000)
            .numberOfMerchants(10)
            .build());

    var response = service.getMetadata("container", "fileName");

    verify(connector).getBlobMetadata(any(), any());
    verify(mapper).getDataSummary(any());
    assertThat(response).isNotNull();
    assertThat(response.getNumberOfMerchants()).isEqualTo(10);
    assertThat(response.getSumAmountPositiveTransactions()).isEqualTo(10000);
  }

  @Test
  @SneakyThrows
  void whenGetMetadataThrowsExceptionThenReturnsInvalidDataSummary() {

    when(connector.getBlobMetadata(any(), any())).thenThrow(new IOException());

    var response = service.getMetadata("container", "fileName");

    verify(connector).getBlobMetadata(any(), any());
    verify(mapper, times(0)).getDataSummary(any());
    assertThat(response).isNotNull();
    assertThat(response.getNumberOfMerchants()).isEqualTo(-1);
    assertThat(response.getSumAmountPositiveTransactions()).isEqualTo(-1);
  }
}