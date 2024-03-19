package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.SenderAdeAckListDto;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface FileReportController {

  @GetMapping(value = "file-report", produces = MediaType.APPLICATION_JSON_VALUE)
  FileReportDto getFileReport(@RequestParam(name = "senderCodes") Collection<String> senderCodes);

  @GetMapping(value = "/sender-ade-ack", produces = MediaType.APPLICATION_JSON_VALUE)
  SenderAdeAckListDto getSenderAdeAckList(
      @NotNull @RequestParam(name = "senderCodes") Collection<String> senderCodes);
}
