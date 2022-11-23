package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import java.util.Collection;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileReportRepositoryImpl implements FileReportRepository {

  @Override
  public Collection<FileReport> getReportsBySenderCodes(Collection<String> senderCodes) {
    // dummy repository
    return Collections.emptyList();
  }
}
