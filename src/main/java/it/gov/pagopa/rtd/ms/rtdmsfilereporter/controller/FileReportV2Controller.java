package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportDto;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface FileReportV2Controller {

  @GetMapping(value = "v2/file-report", produces = MediaType.APPLICATION_JSON_VALUE)
  FileReportDto getFileReportV2(
      @RequestParam(name = "senderCodes") Collection<String> senderCodes);
}
