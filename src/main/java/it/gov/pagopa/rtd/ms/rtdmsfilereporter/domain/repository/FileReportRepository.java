package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository;


import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.Collection;

public interface FileReportRepository {

  Collection<FileReport> getReportsBySenderCodes(Collection<String> senderCodes);

  void save(FileReport fileReport);
}
