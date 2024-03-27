package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class FileMetadataV2Dto {

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

  @NotNull
  private AggregatesDataSummary dataSummary;
}
