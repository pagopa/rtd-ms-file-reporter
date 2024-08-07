package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.FileReportCommandFactory;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.DecryptedEventCommand;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventStatusEnum;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventToDomainMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FileReportEventAdapterTest {

  @Mock
  private FileReportService service;
  @Mock
  private DecryptedEventCommand command;
  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private FileReportEventAdapter adapter;
  private AutoCloseable autoCloseable;
  private final EventToDomainMapper mapper = Mappers.getMapper(EventToDomainMapper.class);
  private final int fileTTL = 10;

  @BeforeEach
  void setUp() {
    adapter = new FileReportEventAdapter(service, validator, mapper,
        new FileReportCommandFactory(command));
    adapter.setFileTimeToLiveInDays(fileTTL);
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
        FileMetadata.builder().name("file").size(100L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate).build());

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnEventThenChangeStatusToFile() {
    var currentDate = LocalDateTime.now();
    FileReport reportToBeRead = FileReport.createFileReport();
    reportToBeRead.addFileUploaded(
        FileMetadata.builder().name("file").size(100L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate).build());
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.of(reportToBeRead));

    // even if other fields are different, the only field that will be changed is "status"
    ProjectorEventDto eventDto = new ProjectorEventDto("file", "12345", 300L, LocalDateTime.now(),
        EventStatusEnum.DECRYPTED);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addFileUploaded(
        FileMetadata.builder().name("file").size(100L).status(FileStatusEnum.VALIDATED_BY_PAGOPA)
            .transmissionDate(currentDate).build());

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnEventThenRemoveOldFile() {
    var currentDate = LocalDateTime.now();
    FileReport reportToBeRead = FileReport.createFileReport();
    reportToBeRead.addFileUploaded(
        FileMetadata.builder().name("file").size(100L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate.minusDays(fileTTL + 1)).build());
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.of(reportToBeRead));

    ProjectorEventDto eventDto = new ProjectorEventDto("newFile", "12345", 300L, currentDate,
        EventStatusEnum.RECEIVED);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addFileUploaded(
        FileMetadata.builder().name("newFile").size(300L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate).build());

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }

  @Test
  void whenReceiveAnEventThenAddNewFile() {
    var currentDate = LocalDateTime.now();
    FileReport reportToBeRead = FileReport.createFileReport();
    reportToBeRead.addFileUploaded(
        FileMetadata.builder().name("file").size(100L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate).build());
    Mockito.when(service.getFileReport(any())).thenReturn(Optional.of(reportToBeRead));

    ProjectorEventDto eventDto = new ProjectorEventDto("newFile", "12345", 300L, currentDate,
        EventStatusEnum.SENT_TO_ADE);
    FileReport expectedReport = FileReport.createFileReport();
    expectedReport.addFileUploaded(
        FileMetadata.builder().name("newFile").size(300L)
            .status(FileStatusEnum.SENT_TO_AGENZIA_DELLE_ENTRATE)
            .transmissionDate(currentDate).build());
    expectedReport.addFileUploaded(
        FileMetadata.builder().name("file").size(100L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate).build());

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
        FileMetadata.builder().name("file").size(100L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(currentDate).build());

    adapter.consumeEvent(eventDto);

    verify(service).getFileReport("12345");
    verify(service).save(expectedReport);
  }
}