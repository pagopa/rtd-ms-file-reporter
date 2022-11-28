package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository;


import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;

public interface FileReportRepository {

  Collection<FileReport> getReportsBySenderCodes(Collection<String> senderCodes);

  @Transactional
  void save(FileReport fileReport);
}
