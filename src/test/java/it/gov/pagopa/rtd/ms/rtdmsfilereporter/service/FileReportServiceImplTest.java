package it.gov.pagopa.rtd.ms.rtdmsfilereporter.service;

import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils.createFileReport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportServiceImpl;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class FileReportServiceImplTest {

  @Mock
  FileReportRepository fileReportRepository;
  FileReportService fileReportService;
  AutoCloseable autoCloseable;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);

    fileReportService = new FileReportServiceImpl(fileReportRepository, any());
  }

  @SneakyThrows
  @AfterEach
  void tearDown() {
    autoCloseable.close();
  }

  @Test
  void whenGetFileReportForManySenderCodesThenMergeTheReportsCorrectly() {
    Mockito.when(fileReportRepository.getReportsBySenderCodes(any())).thenReturn(getReportList());

    FileReport filereport = fileReportService.getAggregateFileReport(
        Collections.singleton("12345"));

    assertThat(filereport).isNotNull();
    assertThat(filereport.getFilesUploaded()).isNotNull().hasSize(4);
    assertThat(filereport.getAckToDownload()).isNotNull().hasSize(6);
  }

  @Test
  void givenEmptyListWhenGetFileReportForManySenderCodesThenReturnsEmptyReport() {
    Mockito.when(fileReportRepository.getReportsBySenderCodes(any()))
        .thenReturn(Collections.emptyList());

    FileReport filereport = fileReportService.getAggregateFileReport(
        Collections.singleton("12345"));

    assertThat(filereport).isNotNull();
    assertThat(filereport.getFilesUploaded()).isNotNull().isEmpty();
    assertThat(filereport.getAckToDownload()).isNotNull().isEmpty();
  }

  @Test
  void whenGetFileReportThenReturnsAReport() {
    Mockito.when(fileReportRepository.getReportBySenderCode(any()))
        .thenReturn(Optional.empty());

    var filereport = fileReportService.getFileReport("12345");

    assertThat(filereport).isEmpty();
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

    var expectedList = reportMock.stream()
        .map(FileReport::getAckToDownload)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
    assertThat(ackToDownloadList)
        .hasSize(expectedList.size())
        .containsExactlyInAnyOrderElementsOf(expectedList);
  }

  @Test
  void whenGetMetadataThenSaveFileReport(){

  }

  Collection<FileReport> getReportList() {
    return Stream.of(createFileReport(3, 1), createFileReport(1, 2), createFileReport(2, 1))
        .collect(Collectors.toList());
  }

}