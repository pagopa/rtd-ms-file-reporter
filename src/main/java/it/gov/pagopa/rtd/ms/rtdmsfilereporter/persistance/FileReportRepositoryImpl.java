package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntityMapper;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileReportRepositoryImpl implements FileReportRepository {

  private final FileReportDao fileReportDao;
  private final FileReportEntityMapper mapper;

  @Override
  public Collection<FileReport> getReportsBySenderCodes(Collection<String> senderCodes) {
    var fileReportEntities = fileReportDao.findBySenderCodeIn(senderCodes);
    if (fileReportEntities.isEmpty()) {
      return Collections.singletonList(FileReport.createFileReport());
    } else {
      return fileReportEntities.stream()
          .map(mapper::entityToDomain)
          .toList();
    }
  }

  @Override
  public Optional<FileReport> getReportBySenderCode(String senderCode) {
    return fileReportDao.findBySenderCode(senderCode)
        .map(mapper::entityToDomain);
  }

  @Override
  public void save(FileReport fileReport) {
    fileReportDao.save(mapper.domainToEntity(fileReport));
  }
}
