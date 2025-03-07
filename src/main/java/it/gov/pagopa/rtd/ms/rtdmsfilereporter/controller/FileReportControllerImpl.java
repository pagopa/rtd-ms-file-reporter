package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.SenderAdeAckListDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1.FileReportDtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportV2Dto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportV2DtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.InputMismatchException;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FileReportControllerImpl implements FileReportController {

  private final FileReportService fileReportService;
  private final FileReportDtoMapper mapper;
  private final FileReportV2DtoMapper mapperV2;

  @Override
  public FileReportDto getFileReport(Collection<String> senderCodes) {
    log.info("GET file report for sender codes: {}", senderCodes.toString());
    return mapper.fileReportToDto(fileReportService.getAggregateFileReport(senderCodes));
  }

  @Override
  public FileReportV2Dto getFileReportV2(Collection<String> senderCodes) {
    log.info("GET file report v2 for sender codes: {}", senderCodes.toString());
    return mapperV2.fileReportToDto(fileReportService.getAggregateFileReport(senderCodes));
  }

  @Override
  public SenderAdeAckListDto getSenderAdeAckList(@NotNull Collection<String> senderCodes) {
    log.info("GET ack to download list for sender codes: {}", senderCodes.toString());
    return SenderAdeAckListDto.builder()
        .fileNameList(fileReportService.getAckToDownloadList(senderCodes))
        .build();
  }

  @Override
  public void saveMetadata(String basePath, String fileName) {
    String sanitizedFileName = fileName.replace("\n", "").replace("\r", "");
    if (validateFileName(sanitizedFileName)) {
      log.info("Enrich metadata on file : {}", sanitizedFileName);
      fileReportService.saveMetadata(basePath, sanitizedFileName);
    } else {
      throw new InputMismatchException("Filename " + sanitizedFileName + " malformed");
    }
  }

  public boolean validateFileName(String filename) {
    // Well formed filename
    // ADE.[Sender Code].TRNLOG.[yyyymmdd].[hhmmss].[acquirer internal id].[chunk number].csv
    String app = filename.substring(0, filename.indexOf(".csv.pgp"));
    Pattern p = Pattern.compile("ADE.\\d{5}.TRNLOG.\\d{8}.\\d{6}.\\d{3}");
    Pattern p2 = Pattern.compile("ADE.\\d{5}.TRNLOG.\\d{8}.\\d{6}.\\d{3}.\\d*");
    return (p.matcher(app).matches() || p2.matcher(app).matches());
  }
}
