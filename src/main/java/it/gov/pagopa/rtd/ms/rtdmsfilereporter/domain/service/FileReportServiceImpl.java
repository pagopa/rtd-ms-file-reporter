package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReportAggregator.aggregateFileReports;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileReportServiceImpl implements FileReportService {

  private final FileReportRepository fileReportRepository;

  @Override
  public FileReport getAggregateFileReport(Collection<String> senderCodes) {
    return fileReportRepository.getReportsBySenderCodes(senderCodes)
        .stream()
        .collect(aggregateFileReports());
  }
}
