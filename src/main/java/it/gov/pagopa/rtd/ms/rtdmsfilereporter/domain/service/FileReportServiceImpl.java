package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception.FileMetadataNotFoundException;


import static it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReportAggregator.aggregateFileReports;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;

import java.util.Collection;
import java.util.Optional;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.ReportFileTTL;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RequiredArgsConstructor
//@EnableConfigurationProperties(ReportFileTTL.class)
public class FileReportServiceImpl implements FileReportService {

    private final FileReportRepository fileReportRepository;
    private final StorageAccountService service;
    private final ReportFileTTL report;

//    @Value("${report.fileTTL}")
//    private Integer fileTimeToLiveInDays;

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
    public void getMetadata(String basePath, String fileName) {
        //get senderCode from filename
        String senderCode = fileName.split("\\.")[1];
        FileReport fileReport = getFileReport(senderCode)
                .orElse(FileReport.createFileReportWithSenderCode(senderCode));

        Optional<FileMetadata> result = fileReport.getFilesUploaded().stream()
                .filter(f -> f.getName().equals(fileName)).findFirst();

        FileMetadata fileMetadata;
        if (result.isEmpty()) {
            String errorMsg = String.format(
                    "File %s not found in latest %d days report of sender %s",
                    fileName, report.fileTTL(), senderCode
            );
            throw new FileMetadataNotFoundException(errorMsg);
        } else
            fileMetadata = result.get();

        basePath = "/" + basePath + "/";
        fileMetadata.setPath(basePath);
        var dataSummary = service.getMetadata(basePath, fileName);

        log.debug("DataSummary : {}", dataSummary.toString());

        fileMetadata.enrichWithSquaringData(dataSummary);
        // two operations are needed: update status + add aggregates summary
        fileReport.addFileOrUpdateStatusIfPresent(fileMetadata);
        fileReport.addSquaringDataToFile(fileMetadata);

        save(fileReport);
    }
}
