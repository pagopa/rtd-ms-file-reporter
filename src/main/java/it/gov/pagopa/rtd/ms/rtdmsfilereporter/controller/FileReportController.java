package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.SenderAdeAckListDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportV2Dto;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface FileReportController {

  @GetMapping(value = "file-report", produces = MediaType.APPLICATION_JSON_VALUE)
  FileReportDto getFileReport(@RequestParam(name = "senderCodes") Collection<String> senderCodes);

  @GetMapping(value = "v2/file-report", produces = MediaType.APPLICATION_JSON_VALUE)
  FileReportV2Dto getFileReportV2(
      @RequestParam(name = "senderCodes") Collection<String> senderCodes);

  @GetMapping(value = "/sender-ade-ack", produces = MediaType.APPLICATION_JSON_VALUE)
  SenderAdeAckListDto getSenderAdeAckList(
      @NotNull @RequestParam(name = "senderCodes") Collection<String> senderCodes);

  @PatchMapping(value = "/report/metadata", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  void saveMetadata(
      @NotNull @RequestParam(name = "basePath") String path,
      @NotNull @RequestParam(name = "fileName") String fileName);
}
