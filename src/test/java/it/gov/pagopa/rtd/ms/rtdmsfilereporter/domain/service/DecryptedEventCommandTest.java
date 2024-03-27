package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;


import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils.createFileReport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DecryptedEventCommandTest {

  private DecryptedEventCommand command;
  @Mock
  private StorageAccountService client;

  @BeforeEach
  void setUp() {
    command = new DecryptedEventCommand(client);
  }

  @Test
  void whenCommandIsExecutedThenMetadataAreAddedToReport() {
    // arrange
    var report = createFileReport(0, 1);
    var metadata = FileMetadata.createNewFileMetadata("filename");
    var dataSummary = AggregatesDataSummary.builder().numberOfMerchants(10)
        .countPositiveTransactions(5).sumAmountPositiveTransactions(100).build();
    Mockito.when(client.getMetadata(any(), any())).thenReturn(dataSummary);

    command.accept(report, metadata);

    Mockito.verify(client, Mockito.times(1)).getMetadata(any(), any());

    // assert metadata has been enriched with stuff
    assertThat(metadata).isNotNull();
    var summary = metadata.getAggregatesDataSummary();
    assertThat(summary).isNotNull();
    assertThat(summary.getCountPositiveTransactions()).isEqualTo(5);
    assertThat(summary.getNumberOfMerchants()).isEqualTo(10);
    assertThat(summary.getSumAmountPositiveTransactions()).isEqualTo(
        100);

    // assert report has been filled with additional data
    assertThat(report).isNotNull();
    var fileData = report.getFilesUploaded().stream().findAny();
    assertThat(fileData).isPresent();
    summary = fileData.get().getAggregatesDataSummary();
    assertThat(summary).isNotNull();
    assertThat(summary.getNumberOfMerchants()).isEqualTo(10);
    assertThat(summary.getSumAmountPositiveTransactions()).isEqualTo(100);
    assertThat(summary.getCountPositiveTransactions()).isEqualTo(5);
  }

  @Test
  void whenDataSummaryIsNullThenThrowsSummaryIsEmpty() {
    var report = createFileReport(0, 1);
    var metadata = FileMetadata.createNewFileMetadata("filename");
    Mockito.when(client.getMetadata(any(), any())).thenReturn(null);

    command.accept(report, metadata);

    assertThat(metadata).isNotNull();
    assertThat(metadata.getAggregatesDataSummary().getCountPositiveTransactions()).isZero();
    assertThat(metadata.getAggregatesDataSummary().getNumberOfMerchants()).isZero();
    assertThat(metadata.getAggregatesDataSummary().getSumAmountPositiveTransactions()).isZero();
  }
}