package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SenderAdeAckListDto {

  @JsonProperty(value = "fileNameList")
  private Collection<String> fileNameList;
}
