package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.Collection;

public interface FileReportService {

  FileReport getAggregateFileReport(Collection<String> senderCodes);

  FileReport getFileReport(String senderCode);

  void save(FileReport fileReport);
}
