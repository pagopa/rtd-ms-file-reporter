package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FileReportRepositoryImpl implements FileReportRepository {

  private final FileReportDao fileReportDao;
  private final ModelMapper modelMapper;

  @Override
  public Collection<FileReport> getReportsBySenderCodes(Collection<String> senderCodes) {
    var fileReportEntities = fileReportDao.findBySenderCodeIn(senderCodes);
    if (fileReportEntities.isEmpty()) {
      return Collections.singletonList(FileReport.createFileReport());
    } else {
      return fileReportEntities.stream().map(entity -> modelMapper.map(entity, FileReport.class))
          .collect(Collectors.toList());
    }
  }
}