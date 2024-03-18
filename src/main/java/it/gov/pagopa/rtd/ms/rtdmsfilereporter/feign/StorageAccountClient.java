package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;

public class StorageAccountClient {

  public AggregatesDataSummary getMetadata(String blobPath) {
    return AggregatesDataSummary.builder().build();
  }
}
