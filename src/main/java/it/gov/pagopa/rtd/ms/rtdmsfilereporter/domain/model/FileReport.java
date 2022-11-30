package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import static java.util.Collections.emptyList;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileReport {

  private String id;

  @NotNull
  @NotBlank
  private Collection<String> senderCodes;

  @NotNull
  private Collection<FileMetadata> filesUploaded;

  @NotNull
  private Collection<String> ackToDownload;

  public void addFileUploaded(FileMetadata fileMetadata) {
    filesUploaded.add(fileMetadata);
  }

  public void removeFileUploaded(@NotNull String name) {
    filesUploaded.removeIf(file -> file.getName().equals(name));
  }

  public void addSenderCode(String senderCode) {
    senderCodes.add(senderCode);
  }

  public void addAckToDownload(String filename) {
    ackToDownload.add(filename);
  }

  public void removeAckToDownload(String filename) {
    ackToDownload.remove(filename);
  }

  public void addFileOrUpdateStatusIfPresent(FileMetadata fileMetadata) {

    if (filesUploaded.stream().anyMatch(file -> file.getName().equals(fileMetadata.getName()))) {
      filesUploaded.stream()
          .filter(file -> file.getName().equals(fileMetadata.getName()))
          .forEach(file -> file.setStatus(fileMetadata.getStatus()));
    } else {
      filesUploaded.add(fileMetadata);
    }
  }

  public void removeFilesOlderThan(int days) {
    filesUploaded.removeIf(
        file -> file.getTransmissionDate().isBefore(LocalDateTime.now().minusDays(days)));
  }

  public static FileReport createFileReport() {
    return new FileReport(null, new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  public static FileReport createFileReportWithSenderCode(String senderCode) {
    var fileReport = createFileReport();
    fileReport.addSenderCode(senderCode);
    return fileReport;
  }

  public static FileReport mergeFileReports(FileReport firstReport, FileReport secondReport) {
    firstReport.setId(null);
    firstReport.getSenderCodes().addAll(
        Objects.requireNonNullElse(secondReport.getSenderCodes(), emptyList()));
    firstReport.getFilesUploaded().addAll(
        Objects.requireNonNullElse(secondReport.getFilesUploaded(), emptyList()));
    firstReport.getAckToDownload().addAll(
        Objects.requireNonNullElse(secondReport.getAckToDownload(), emptyList()));
    return firstReport;
  }
}
