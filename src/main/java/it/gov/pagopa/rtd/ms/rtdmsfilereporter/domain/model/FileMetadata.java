package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FileMetadata {

  @NotNull
  @NotBlank
  private String name;

  private Long size;

  @NotNull
  @NotBlank
  private String status;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime transmissionDate;

  public static FileMetadata createNewFileMetadata(String name) {
    return createNewFileMetadataWithStatus(name, null);
  }

  public static FileMetadata createNewFileMetadataWithStatus(String name, String status) {
    return new FileMetadata(name, null, status, LocalDateTime.now());
  }
}
