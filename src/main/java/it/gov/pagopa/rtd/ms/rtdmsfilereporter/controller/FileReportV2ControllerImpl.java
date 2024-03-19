package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportV2DtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileReportV2ControllerImpl implements FileReportV2Controller {

  private final FileReportService fileReportService;
  private final FileReportV2DtoMapper mapperV2;

  @Override
  public FileReportDto getFileReportV2(Collection<String> senderCodes) {
    log.info("GET file report v2 for sender codes: {}", senderCodes.toString());
    return mapperV2.fileReportToDto(fileReportService.getAggregateFileReport(senderCodes));
  }
}
