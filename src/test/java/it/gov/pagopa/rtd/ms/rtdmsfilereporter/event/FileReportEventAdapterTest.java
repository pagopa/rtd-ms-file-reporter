package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.FileReportCommandFactory;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventStatusEnum;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class FileReportEventAdapterTest {

  @Mock
  private FileReportService service;
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private FileReportEventAdapter adapter;
  private AutoCloseable autoCloseable;
  private final int fileTTL = 10;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    adapter = new FileReportEventAdapter(service, validator, new FileReportCommandFactory());
    adapter.setFileTimeToLiveInDays(fileTTL);
  }

  @SneakyThrows
  @AfterEach
  void tearDown() {
    autoCloseable.close();
  }

  @Test
  void whenReceiveAMalformedEventThenRaiseException() {
    ProjectorEventDto eventDto = new ProjectorEventDto("file", null, 100L, LocalDateTime.now(),
        EventStatusEnum.RECEIVED);

    assertThatThrownBy(() -> adapter.consumeEvent(eventDto)).isInstanceOf(
        ConstraintViolationException.class);
  }

  @Test
  void whenReceiveAnEventThenAddFileToEmptyReport() {
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.empty());

    var currentDate = LocalDateTime.now();
    ProjectorEventDto eventDto = new ProjectorEventDto("file", "12345", 100L, currentDate,
        EventStatusEnum.RECEIVED);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.setSenderCodes(Collections.singleton("12345"));
    expectedReport.addFileUploaded(
        new FileMetadata("file", 100L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate));

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnEventThenChangeStatusToFile() {
    var currentDate = LocalDateTime.now();
    FileReport reportToBeRead = FileReport.createFileReport();
    reportToBeRead.addFileUploaded(
        new FileMetadata("file", 100L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate));
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.of(reportToBeRead));

    // even if other fields are different, the only field that will be changed is "status"
    ProjectorEventDto eventDto = new ProjectorEventDto("file", "12345", 300L, LocalDateTime.now(),
        EventStatusEnum.DECRYPTED);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addFileUploaded(
        new FileMetadata("file", 100L, FileStatusEnum.VALIDATED_BY_PAGOPA, currentDate));

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnEventThenRemoveOldFile() {
    var currentDate = LocalDateTime.now();
    FileReport reportToBeRead = FileReport.createFileReport();
    reportToBeRead.addFileUploaded(
        new FileMetadata("file", 100L, FileStatusEnum.RECEIVED_BY_PAGOPA,
            currentDate.minusDays(fileTTL + 1)));
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.of(reportToBeRead));

    ProjectorEventDto eventDto = new ProjectorEventDto("newFile", "12345", 300L, currentDate,
        EventStatusEnum.RECEIVED);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addFileUploaded(
        new FileMetadata("newFile", 300L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate));

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnEventThenAddNewFile() {
    var currentDate = LocalDateTime.now();
    FileReport reportToBeRead = FileReport.createFileReport();
    reportToBeRead.addFileUploaded(
        new FileMetadata("file", 100L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate));
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.of(reportToBeRead));

    ProjectorEventDto eventDto = new ProjectorEventDto("newFile", "12345", 300L, currentDate,
        EventStatusEnum.SENT_TO_ADE);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addFileUploaded(
        new FileMetadata("newFile", 300L, FileStatusEnum.SENT_TO_AGENZIA_DELLE_ENTRATE, currentDate));
    expectedReport.addFileUploaded(
        new FileMetadata("file", 100L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate));

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnACKEventThenAddNewAckToEmptyReport() {
    var currentDate = LocalDateTime.now();
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.empty());

    ProjectorEventDto eventDto = new ProjectorEventDto("ackFile", "12345", 300L, currentDate,
        EventStatusEnum.ACK_TO_DOWNLOAD);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addSenderCode("12345");
    expectedReport.addAckToDownload("ackFile");

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnACKEventThenRemoveAckFromCollection() {
    var currentDate = LocalDateTime.now();
    FileReport reportToBeRead = FileReport.createFileReport();
    reportToBeRead.addAckToDownload("ackToDownload");
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.of(reportToBeRead));

    ProjectorEventDto eventDto = new ProjectorEventDto("ackToDownload", "12345", 300L, currentDate,
        EventStatusEnum.ACK_DOWNLOADED);
    FileReport expectedReport = FileReport.createFileReport();

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveStatusNotMappedThenThrowException() {
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.empty());

    var currentDate = LocalDateTime.now();
    ProjectorEventDto eventDto = new ProjectorEventDto("file", "12345", 100L, currentDate,
        EventStatusEnum.RECEIVED);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addSenderCode("12345");
    expectedReport.addFileUploaded(
        new FileMetadata("file", 100L, FileStatusEnum.RECEIVED_BY_PAGOPA, currentDate));

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }
}