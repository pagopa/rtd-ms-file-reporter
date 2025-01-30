package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReportAggregator.aggregateFileReports;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;


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
  public void getMetadata(String filePath){

    String[] af = filePath.split("/");
    String basePath = filePath.substring(0, filePath.indexOf("/AGGADE"));
    String fileName = af[af.length-1];

    FileMetadata fileMetadata = FileMetadata.createNewFileMetadata(fileName);

    var dataSummary = service.getMetadata(basePath,fileName);

    //get senderCode from filename
    String senderCode = fileName.split("\\.")[1];
    var fileReport = getFileReport(senderCode)
            .orElse(FileReport.createFileReportWithSenderCode(senderCode));

    // actions on domain object
    fileMetadata.enrichWithSquaringData(dataSummary);
    // two operations are needed: update status + add aggregates summary
    fileReport.addFileOrUpdateStatusIfPresent(fileMetadata);
    fileReport.addSquaringDataToFile(fileMetadata);
  }
}
