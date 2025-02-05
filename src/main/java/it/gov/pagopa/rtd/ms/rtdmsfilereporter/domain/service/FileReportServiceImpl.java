package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReportAggregator.aggregateFileReports;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
public class FileReportServiceImpl implements FileReportService {

  private final FileReportRepository fileReportRepository;
  private final StorageAccountService service;

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

  @Override
  public void getMetadata(String basePath, String fileName){
    FileMetadata fileMetadata = FileMetadata.createNewFileMetadata(fileName);
    basePath = "/" + basePath + "/";
    fileMetadata.setPath(basePath);

    var dataSummary = service.getMetadata(basePath,fileName);

    log.debug("DataSummay : " + dataSummary.toString());
    //get senderCode from filename
    String senderCode = fileName.split("\\.")[1];
    FileReport fileReport = getFileReport(senderCode)
            .orElse(FileReport.createFileReportWithSenderCode(senderCode));

    // actions on domain object
    fileMetadata.enrichWithSquaringData(dataSummary);
    // two operations are needed: update status + add aggregates summary
    fileReport.addFileOrUpdateStatusIfPresent(fileMetadata);
    fileReport.addSquaringDataToFile(fileMetadata);

    save(fileReport);
  }
}
