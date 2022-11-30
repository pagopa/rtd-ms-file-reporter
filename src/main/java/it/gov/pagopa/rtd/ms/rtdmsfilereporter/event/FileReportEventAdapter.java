package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.FileReportStrategy;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventToDomainMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
  private final ModelMapper modelMapper = EventToDomainMapper.createEventDomainMapper();
  private final FileReportStrategy fileReportStrategy;

  public void consumeEvent(@Valid ProjectorEventDto eventDto) {
    // validate dto
    var validationErrors = validator.validate(eventDto);
    if (!validationErrors.isEmpty()) {
      throw new ConstraintViolationException(validationErrors);
    }

    // retrieve the report
    var report = service.getFileReport(eventDto.getSender())
        .orElse(FileReport.createFileReportWithSenderCode(eventDto.getSender()));

    // execute policy on report
    fileReportStrategy.getCommandByStatus(eventDto.getStatus().name())
        .accept(report, modelMapper.map(eventDto, FileMetadata.class));

    // remove from the report the old files
    report.removeFilesOlderThan(fileTimeToLiveInDays);

    // save the report
    service.save(report);
  }
}
