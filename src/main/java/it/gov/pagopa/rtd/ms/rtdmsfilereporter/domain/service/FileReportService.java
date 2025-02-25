package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.Collection;
import java.util.Optional;

public interface FileReportService {

  FileReport getAggregateFileReport(Collection<String> senderCodes);

  Optional<FileReport> getFileReport(String senderCode);

  Collection<String> getAckToDownloadList(Collection<String> senderCodes);

  void save(FileReport fileReport);

  void saveMetadata(String basePath, String fileName);
}
