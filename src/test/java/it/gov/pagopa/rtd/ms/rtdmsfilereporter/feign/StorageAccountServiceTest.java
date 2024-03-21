package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class StorageAccountServiceTest {

  @Mock
  private StorageAccountRestConnector connector;
  private StorageAccountService service;

//  @BeforeEach
//  void setUp() {
//    service = new StorageAccountService(connector);
//  }
//
//  @Test
//  void whenGetMetadataThenReturnsDataSummary() {
//
//    Mockito.when(connector.getBlobMetadata(any(), any()))
//        .thenReturn(ResponseEntity.status(200).header("x-meta-key:value").build());
//
//    var response = service.getMetadata("container", "fileName");
//
//    assertThat(response).isNotNull();
//    assertThat(response.getNumberOfMerchants()).isEqualTo(10);
//    assertThat(response.getSumAmountPositiveTransactions()).isEqualTo(10000);
//  }
}