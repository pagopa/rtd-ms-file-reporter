package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Builder
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
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  private LocalDateTime transmissionDate;

  public static FileMetadata createNewFileMetadata(String name) {
    return createNewFileMetadataWithStatus(name, null);
  }

  public static FileMetadata createNewFileMetadataWithStatus(String name, String status) {
    return FileMetadata.builder()
        .name(name)
        .status(status)
        .transmissionDate(LocalDateTime.now())
        .build();
  }
}
