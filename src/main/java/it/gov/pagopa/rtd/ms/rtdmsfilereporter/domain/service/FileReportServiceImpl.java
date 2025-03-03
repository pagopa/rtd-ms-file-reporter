package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception.FileMetadataNotFoundException;

import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReportAggregator.aggregateFileReports;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Optional;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.ReportConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FileReportServiceImpl implements FileReportService {

  private final FileReportRepository fileReportRepository;
  private final StorageAccountService service;
  private final ReportConfiguration reportConfig;

  @Override
  public FileReport getAggregateFileReport(Collection<String> senderCodes) {
    return fileReportRepository.getReportsBySenderCodes(senderCodes).stream()
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

  @Override
  public void saveMetadata(String basePath, String fileName) {
    // get senderCode from filename
    String senderCode = fileName.split("\\.")[1];
    FileReport fileReport = getFileReport(senderCode).orElseThrow();

    Optional<FileMetadata> result =
        fileReport.getFilesUploaded().stream()
            .filter(f -> f.getName().equals(fileName))
            .findFirst();

    FileMetadata fileMetadata;
    if (result.isEmpty()) {
      String errorMsg =
          String.format(
              "File %s not found in latest %d days report of sender %s",
              fileName, reportConfig.fileTTL(), senderCode);
      throw new FileMetadataNotFoundException(errorMsg);
    } else fileMetadata = result.get();

    String path = File.separator + basePath + File.separator;
    fileMetadata.setPath(path);
    var dataSummary = service.getMetadata(path, fileName);
    log.debug("DataSummary : {}", dataSummary.toString());
    fileMetadata.enrichWithSquaringData(dataSummary);
    save(fileReport);
  }
}
