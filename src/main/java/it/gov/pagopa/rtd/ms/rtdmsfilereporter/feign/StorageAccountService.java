package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class StorageAccountService {

  private final StorageAccountRestConnector connector;
  private final AggregatesDataSummaryMapper mapper;

  public AggregatesDataSummary getMetadata(String basePath, String fileName) {
    Map<String, String> response;
    try {
      response = connector.getBlobMetadata(basePath, fileName);
    } catch (IOException e) {
      log.warn("Failed to retrieve the file metadata from the storage!");
      return AggregatesDataSummary.createInvalidDataSummary();
    }

    response.forEach((key, value) -> log.info("header {} with value {}", key, value));

    return mapper.getDataSummary(response);
  }
}
