package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.FileReportCommandFactory;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventToDomainMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Setter
public class FileReportEventAdapter {

  @Value("${report.fileTTL}")
  private Integer fileTimeToLiveInDays;

  private final FileReportService service;
  private final Validator validator;
  private final EventToDomainMapper mapper;
  private final FileReportCommandFactory fileReportCommandFactory;

  public void consumeEvent(@Valid ProjectorEventDto eventDto) {
    // validate dto
    var validationErrors = validator.validate(eventDto);
    if (!validationErrors.isEmpty()) {
      throw new ConstraintViolationException(validationErrors);
    }
    log.info("Received event for file {} with status {}", eventDto.getFilePath(), eventDto.getStatus().name());

    // retrieve the report
    var report = service.getFileReport(eventDto.getSender())
        .orElse(FileReport.createFileReportWithSenderCode(eventDto.getSender()));

    // execute command on report
    fileReportCommandFactory.getCommandByStatus(eventDto.getStatus().name())
        .accept(report, mapper.eventToDomain(eventDto));

    // remove from the report the old files
    report.removeFilesOlderThan(fileTimeToLiveInDays);

    // save the report
    service.save(report);
  }
}
