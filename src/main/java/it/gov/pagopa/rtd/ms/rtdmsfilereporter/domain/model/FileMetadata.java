package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FileMetadata implements Comparable<FileMetadata> {

  @NotNull
  @NotBlank
  private String name;

  private Long size;

  @NotNull
  @NotBlank
  private FileStatusEnum status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime transmissionDate;

  public static FileMetadata createNewFileMetadata(String name) {
    return createNewFileMetadataWithStatus(name, null);
  }

  public static FileMetadata createNewFileMetadataWithStatus(String name, FileStatusEnum status) {
    return new FileMetadata(name, null, status, LocalDateTime.now());
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
}
