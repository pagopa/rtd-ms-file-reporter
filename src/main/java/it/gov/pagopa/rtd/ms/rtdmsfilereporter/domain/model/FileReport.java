package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import static java.util.Collections.emptyList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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

  public void updateFileStatus(@NotNull String filename, String status) {

    if (filesUploaded.stream().anyMatch(file -> file.getName().equals(filename))) {
      filesUploaded.stream().filter(file -> file.getName().equals(filename))
          .forEach(file -> file.setStatus(status));
    } else {
      FileMetadata fileMetadata = FileMetadata.createNewFileMetadataWithStatus(filename, status);
      filesUploaded.add(fileMetadata);
    }
  }

  public void addAckToDownload(String filename) {
    ackToDownload.add(filename);
  }

  public void removeAckToDownload(String filename) {
    ackToDownload.remove(filename);
  }

  public static FileReport createFileReport() {
    return new FileReport(null, new HashSet<>(), new HashSet<>(), new HashSet<>());
  }

  public static FileReport sumFileReports(FileReport firstReport, FileReport secondReport) {
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
