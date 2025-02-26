package it.gov.pagopa.rtd.ms.rtdmsfilereporter.service;

import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils.createFileReport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception.FileMetadataNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportServiceImpl;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.StorageAccountService;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.ReportConfiguration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class FileReportServiceImplTest {

  @Mock FileReportRepository fileReportRepository;

  @Mock StorageAccountService storageAccountService;

  private final ReportConfiguration reportConfiguration = new ReportConfiguration(15);

  FileReportService fileReportService;
  AutoCloseable autoCloseable;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);

    fileReportService =
        new FileReportServiceImpl(fileReportRepository, storageAccountService, reportConfiguration);
  }

  @SneakyThrows
  @AfterEach
  void tearDown() {
    autoCloseable.close();
  }

  @Test
  void whenGetFileReportForManySenderCodesThenMergeTheReportsCorrectly() {
    Mockito.when(fileReportRepository.getReportsBySenderCodes(any())).thenReturn(getReportList());

    FileReport filereport =
        fileReportService.getAggregateFileReport(Collections.singleton("12345"));

    assertThat(filereport).isNotNull();
    assertThat(filereport.getFilesUploaded()).isNotNull().hasSize(4);
    assertThat(filereport.getAckToDownload()).isNotNull().hasSize(6);
  }

  @Test
  void givenEmptyListWhenGetFileReportForManySenderCodesThenReturnsEmptyReport() {
    Mockito.when(fileReportRepository.getReportsBySenderCodes(any()))
        .thenReturn(Collections.emptyList());

    FileReport filereport =
        fileReportService.getAggregateFileReport(Collections.singleton("12345"));

    assertThat(filereport).isNotNull();
    assertThat(filereport.getFilesUploaded()).isNotNull().isEmpty();
    assertThat(filereport.getAckToDownload()).isNotNull().isEmpty();
  }

  @Test
  void whenGetFileReportThenReturnsAReport() {
    Mockito.when(fileReportRepository.getReportBySenderCode(any())).thenReturn(Optional.empty());

    var fileReport = fileReportService.getFileReport("12345");

    assertThat(fileReport).isEmpty();
  }

  @Test
  void whenSaveThenRepositorySaveIsInvoked() {
    fileReportService.save(FileReport.createFileReport());

    verify(fileReportRepository).save(FileReport.createFileReport());
  }

  @Test
  void whenGetAckToDownloadThenReturnsACollectionOfStrings() {
    var reportMock = getReportList();
    Mockito.when(fileReportRepository.getReportsBySenderCodes(any())).thenReturn(reportMock);

    var ackToDownloadList = fileReportService.getAckToDownloadList(Collections.singleton("12345"));

    var expectedList =
        reportMock.stream()
            .map(FileReport::getAckToDownload)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    assertThat(ackToDownloadList)
        .hasSize(expectedList.size())
        .containsExactlyInAnyOrderElementsOf(expectedList);
  }

  Collection<FileReport> getReportList() {
    return Stream.of(createFileReport(3, 1), createFileReport(1, 2), createFileReport(2, 1))
        .toList();
  }

  @Test
  void whenSaveMetadataGivenWellFormedFilenameThenExtractSenderCodeAndSave() {
    String basePath = "basePath";
    String fileName = "ADE.12345.TRNLOG.20230101.130000.001.01.csv";
    String senderCode = "12345";

    FileReport fileReport = FileReport.createFileReportWithSenderCode(senderCode);

    var fileMetadata = FileMetadata.createNewFileMetadata(fileName);
    fileReport.addFileOrUpdateStatusIfPresent(fileMetadata);

    Mockito.when(fileReportService.getFileReport(senderCode)).thenReturn(Optional.of(fileReport));

    AggregatesDataSummary dataSummaryMock =
        AggregatesDataSummary.builder()
            .countPositiveTransactions(100)
            .sumAmountPositiveTransactions(1000)
            .build();
    Mockito.when(storageAccountService.getMetadata("/" + basePath + "/", fileName))
        .thenReturn(dataSummaryMock);

    fileReportService.saveMetadata(basePath, fileName);

    Mockito.verify(fileReportRepository).save(fileReport);
    assertThat(fileMetadata.getPath()).isEqualTo("/" + basePath + "/");
  }

  @Test
  void whenSaveMetadataGivenNoMatchingFileMetadataThenThrowNotFound() {

    String basePath = "basePath";
    String fileName = "ADE.12345.TRNLOG.20230101.130000.001.01.csv";
    String senderCode = "12345";

    FileReport fileReport = FileReport.createFileReportWithSenderCode(senderCode);

    Mockito.when(fileReportService.getFileReport(senderCode)).thenReturn(Optional.of(fileReport));

    assertThatThrownBy(() -> fileReportService.saveMetadata(basePath, fileName))
        .isInstanceOf(FileMetadataNotFoundException.class)
        .hasMessageContaining("not found in latest")
        .hasMessageContaining(fileName)
        .hasMessageContaining(senderCode);

    Mockito.verify(fileReportRepository, Mockito.never()).save(any());
  }
}
