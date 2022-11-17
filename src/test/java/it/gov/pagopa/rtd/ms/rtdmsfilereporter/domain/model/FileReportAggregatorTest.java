package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class FileReportAggregatorTest {

  @Test
  void whenGetFileReportForMoreSenderCodesThenMergeTheReportsCorrectly() {

    FileReport filereport = getReportList()
        .stream()
        .collect(FileReportAggregator.aggregateFileReports());

    assertThat(filereport).isNotNull();
    assertThat(filereport.getFilesUploaded())
        .hasSize(4)
        .map(FileMetadata::getName).contains("file1", "file2", "file3", "file4");
    assertThat(filereport.getAckToDownload())
        .hasSize(3)
        .contains("ackreport1", "ackreport2", "ackreport3");
  }

  FileReport createFirstFileReport() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addAckToDownload("ackreport1");
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file1"));
    return fileReport;
  }

  FileReport createSecondFileReport() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addAckToDownload("ackreport2");
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file2"));
    return fileReport;
  }

  FileReport createThirdFileReport() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addAckToDownload("ackreport3");
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file3"));
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file4"));
    return fileReport;
  }

  Collection<FileReport> getReportList() {
    return Stream.of(createFirstFileReport(), createSecondFileReport(), createThirdFileReport())
        .collect(Collectors.toList());
  }
}