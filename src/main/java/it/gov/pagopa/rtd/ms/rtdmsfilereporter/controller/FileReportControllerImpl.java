package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileReportDtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.SenderAdeAckListDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileReportControllerImpl implements FileReportController {

  private final FileReportService fileReportService;
  private final ModelMapper modelMapper = FileReportDtoMapper.createDtoDomainMapper();

  @Override
  public FileReportDto getFileReport(Collection<String> senderCodes) {
    log.info("GET file report for sender codes: {}", senderCodes.toString());
    return modelMapper.map(fileReportService.getAggregateFileReport(senderCodes),
        FileReportDto.class);
  }

  @Override
  public SenderAdeAckListDto getSenderAdeAckList(@NotNull Collection<String> senderCodes) {
    log.info("GET ack to download list for sender codes: {}", senderCodes.toString());
    return SenderAdeAckListDto.builder()
        .fileNameList(fileReportService.getAckToDownloadList(senderCodes))
        .build();
  }
}
