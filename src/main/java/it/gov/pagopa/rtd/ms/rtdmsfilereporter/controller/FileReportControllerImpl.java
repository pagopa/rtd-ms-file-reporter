package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FileReportControllerImpl implements FileReportController {

  private final FileReportService fileReportService;
  private final ModelMapper modelMapper;

  @Override
  public FileReportDto getFileReport(Collection<String> senderCodes) {
    return modelMapper.map(fileReportService.getFileReport(senderCodes), FileReportDto.class);
  }
}
