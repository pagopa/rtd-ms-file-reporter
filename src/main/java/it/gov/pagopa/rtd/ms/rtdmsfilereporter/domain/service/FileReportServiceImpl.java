package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReportAggregator.aggregateFileReports;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class FileReportServiceImpl implements FileReportService {

  private final FileReportRepository fileReportRepository;

  @Override
  public FileReport getAggregateFileReport(Collection<String> senderCodes) {
    return fileReportRepository.getReportsBySenderCodes(senderCodes)
        .stream()
        .collect(aggregateFileReports());
  }

  @Override
  public Optional<FileReport> getFileReport(String senderCode) {
    return fileReportRepository.getReportBySenderCode(senderCode);
  }

  @Override
  public Collection<String> getAckToDownloadList(Collection<String> senderCodes) {
    return getAggregateFileReport(senderCodes).getAckToDownload();
  }

  @Override
  public void save(FileReport fileReport) {
    fileReportRepository.save(fileReport);
  }
}
