package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository;


import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.Collection;
import java.util.Optional;

public interface FileReportRepository {

  Collection<FileReport> getReportsBySenderCodes(Collection<String> senderCodes);

  Optional<FileReport> getReportBySenderCode(String senderCode);

  void save(FileReport fileReport);
}
