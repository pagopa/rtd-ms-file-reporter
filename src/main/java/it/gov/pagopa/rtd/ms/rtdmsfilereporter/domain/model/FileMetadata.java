package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class FileMetadata implements Comparable<FileMetadata> {

  @NotNull
  @NotBlank
  private String name;

  @NotNull
  private String path;

  private Long size;

  @NotNull
  @NotBlank
  private FileStatusEnum status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime transmissionDate;

  private AggregatesDataSummary aggregatesDataSummary;

  public String getCompletePath() {
    return this.path + this.name;
  }

  public static FileMetadata createNewFileMetadata(String name) {
    return createNewFileMetadataWithStatus(name, null);
  }

  public static FileMetadata createNewFileMetadataWithStatus(String name, FileStatusEnum status) {
    return new FileMetadata(name, null, null, status, LocalDateTime.now(), null);
  }

  /**
   * Descending ordering for transmissionDate. The files will be ordered from the most recent to the
   * oldest.
   *
   * @param o the object to be compared.
   * @return result of compare.
   */
  @Override
  public int compareTo(@NotNull FileMetadata o) {
    return o.getTransmissionDate().isEqual(this.transmissionDate) ? o.getName()
        .compareTo(this.name) : o.getTransmissionDate().compareTo(this.transmissionDate);
  }

  /**
   * Enrich the file metadata with the summary of the content.
   * @param dataSummary additional information about content of file
   */
  public void enrichWithSquaringData(@NotNull AggregatesDataSummary dataSummary) {
    this.aggregatesDataSummary = dataSummary;
  }
}
