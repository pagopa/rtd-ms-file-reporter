package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntity;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntityMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

class FileReportRepositoryImplTest {

  @Mock
  FileReportDao dao;

  FileReportRepository repository;

  ModelMapper modelMapper = FileReportEntityMapper.createEntityDomainMapper();
  AutoCloseable autoCloseable;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    repository = new FileReportRepositoryImpl(dao, modelMapper);
  }

  @SneakyThrows
  @AfterEach
  void tearDown() {
    autoCloseable.close();
  }

  @Test
  void givenNoReportsWhenGetReportsThenReturnEmptyReport() {
    Mockito.when(dao.findBySenderCodeIn(any())).thenReturn(Collections.emptyList());

    var fileReports = repository.getReportsBySenderCodes(any());

    assertThat(fileReports).isNotNull().hasSize(1);
    fileReports.forEach(report -> {
      assertThat(report.getAckToDownload()).isNotNull().isEmpty();
      assertThat(report.getFilesUploaded()).isNotNull().isEmpty();
    });
  }

  @Test
  void givenTwoReportsWhenGetReportsThenReturnsListOfReports() {
    Mockito.when(dao.findBySenderCodeIn(any())).thenReturn(getMockedReports());

    var fileReports = repository.getReportsBySenderCodes(any());

    assertThat(fileReports).isNotNull().hasSize(2);
  }

  @Test
  void givenNoReportWhenGetReportThenReturnEmptyReport() {
    Mockito.when(dao.findBySenderCode("12345")).thenReturn(Optional.empty());

    var fileReport = repository.getReportBySenderCode("12345");

    assertThat(fileReport).isNotNull();
    assertThat(fileReport.getAckToDownload()).isNotNull().isEmpty();
    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();
    assertThat(fileReport.getSenderCodes()).isNotNull().isEmpty();
  }

  @Test
  void whenGetReportThenReturnAReport() {
    var reportStub = TestUtils.createFileReport(1, 1);
    reportStub.setSenderCodes(Collections.singleton("12345"));
    Mockito.when(dao.findBySenderCode("12345")).thenReturn(
        Optional.of(modelMapper.map(reportStub, FileReportEntity.class)));

    var fileReport = repository.getReportBySenderCode("12345");

    assertThat(fileReport).isNotNull();
    assertThat(fileReport.getAckToDownload()).isNotNull().hasSize(1);
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1);
    assertThat(fileReport.getSenderCodes()).isNotNull().contains("12345");
  }

  @Test
  void mappingFromEntityToDomainWorksCorrectly() {
    var currentDate = LocalDateTime.now();
    FileReportEntity fileReportEntity = new FileReportEntity();
    fileReportEntity.setSenderCode("12345");
    fileReportEntity.setFilesUploaded(
        List.of(new FileMetadata("file", 200L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate)));
    fileReportEntity.setAckToDownload(List.of("ack1", "ack2"));
    fileReportEntity.setId("testID");

    var fileReport = modelMapper.map(fileReportEntity, FileReport.class);

    assertThat(fileReport).isNotNull();
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1)
        .containsExactly(
            new FileMetadata("file", 200L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate));
    assertThat(fileReport.getAckToDownload()).isNotNull().hasSize(2).contains("ack1", "ack2");
    assertThat(fileReport.getSenderCodes()).isNotNull().hasSize(1).contains("12345");
    assertThat(fileReport.getId()).isNotNull().isEqualTo("testID");
  }

  @Test
  void whenSaveReportThenSaveIt() {
    var reportStub = TestUtils.createFileReport(1, 1);
    reportStub.setSenderCodes(Collections.singleton("12345"));

    repository.save(reportStub);

    verify(dao).save(modelMapper.map(reportStub, FileReportEntity.class));
  }

  Collection<FileReportEntity> getMockedReports() {
    var reports = new ArrayList<FileReportEntity>();
    reports.add(modelMapper.map(TestUtils.createFileReport(1, 1), FileReportEntity.class));
    reports.add(modelMapper.map(TestUtils.createFileReport(2, 1), FileReportEntity.class));
    return reports;
  }

}