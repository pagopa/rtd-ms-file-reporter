package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class StorageAccountService {

  private final StorageAccountRestConnector connector;

  public AggregatesDataSummary getMetadata(String blobContainer, String fileName) {

    var response = connector.getBlobMetadata(blobContainer, fileName);

    // todo extract data from response entity
    log.info("response for {} has status {}", fileName, response.getStatusCode());

    return AggregatesDataSummary.builder().build();
  }
}
