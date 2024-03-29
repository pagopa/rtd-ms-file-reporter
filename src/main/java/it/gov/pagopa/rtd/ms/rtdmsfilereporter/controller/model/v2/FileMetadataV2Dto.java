package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1.FileMetadataDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class FileMetadataV2Dto extends FileMetadataDto {

  @NotNull
  private AggregatesDataSummary dataSummary;
}
