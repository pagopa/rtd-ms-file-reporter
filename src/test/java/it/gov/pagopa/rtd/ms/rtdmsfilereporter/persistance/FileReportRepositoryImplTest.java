package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

  ModelMapper modelMapper = new ModelMapper();
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

    Collection<FileReport> fileReports = repository.getReportsBySenderCodes(any());

    assertThat(fileReports).isNotNull().hasSize(2);
  }

  Collection<FileReportEntity> getMockedReports() {
    var reports = new ArrayList<FileReportEntity>();
    reports.add(modelMapper.map(TestUtils.createFileReport(1, 1), FileReportEntity.class));
    reports.add(modelMapper.map(TestUtils.createFileReport(2, 1), FileReportEntity.class));
    return reports;
  }

}