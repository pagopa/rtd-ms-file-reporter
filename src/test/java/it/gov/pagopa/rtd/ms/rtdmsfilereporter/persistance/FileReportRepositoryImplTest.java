package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
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
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class FileReportRepositoryImplTest {

  @Mock
  FileReportDao dao;

  FileReportRepository repository;

  FileReportEntityMapper mapper = Mappers.getMapper(FileReportEntityMapper.class);
  AutoCloseable autoCloseable;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    repository = new FileReportRepositoryImpl(dao, mapper);
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

    assertThat(fileReport).isEmpty();
  }

  @Test
  void whenGetReportThenReturnAReport() {
    var reportStub = TestUtils.createFileReport(1, 1);
    reportStub.setSenderCodes(Collections.singleton("12345"));
    Mockito.when(dao.findBySenderCode("12345")).thenReturn(
        Optional.of(mapper.domainToEntity(reportStub)));

    var fileReport = repository.getReportBySenderCode("12345");

    assertThat(fileReport).isNotEmpty();
    assertThat(fileReport.get().getAckToDownload()).isNotNull().hasSize(1);
    assertThat(fileReport.get().getFilesUploaded()).isNotNull().hasSize(1);
    assertThat(fileReport.get().getSenderCodes()).isNotNull().contains("12345");
  }

  @Test
  void mappingFromEntityToDomainWorksCorrectly() {
    var currentDate = LocalDateTime.now();
    FileReportEntity fileReportEntity = new FileReportEntity();
    fileReportEntity.setSenderCode("12345");
    fileReportEntity.setFilesUploaded(
        List.of(FileMetadata.builder().name("file").size(200L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate).build()));
    fileReportEntity.setAckToDownload(List.of("ack1", "ack2"));
    fileReportEntity.setId("testID");

    var fileReport = mapper.entityToDomain(fileReportEntity);

    assertThat(fileReport).isNotNull();
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1)
        .containsExactly(
            FileMetadata.builder().name("file").size(200L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
                .transmissionDate(currentDate).build());
    assertThat(fileReport.getAckToDownload()).isNotNull().hasSize(2).contains("ack1", "ack2");
    assertThat(fileReport.getSenderCodes()).isNotNull().hasSize(1).contains("12345");
    assertThat(fileReport.getId()).isNotNull().isEqualTo("testID");
  }

  @Test
  void whenSaveReportThenSaveIt() {
    var reportStub = TestUtils.createFileReport(1, 1);
    reportStub.setSenderCodes(Collections.singleton("12345"));

    repository.save(reportStub);

    verify(dao).save(mapper.domainToEntity(reportStub));
  }

  Collection<FileReportEntity> getMockedReports() {
    var reports = new ArrayList<FileReportEntity>();
    reports.add(mapper.domainToEntity(TestUtils.createFileReport(1, 1)));
    reports.add(mapper.domainToEntity(TestUtils.createFileReport(2, 1)));
    return reports;
  }

}