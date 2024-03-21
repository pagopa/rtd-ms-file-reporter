package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class StorageAccountService {

  private final StorageAccountRestConnectorImpl connector;

  public AggregatesDataSummary getMetadata(String basePath, String fileName) throws IOException {
    log.info("passing container {} and name {}", basePath, fileName);
    var response = connector.getBlobMetadata(basePath, fileName);

    // todo extract data from response entity
//    log.info("response for {} has status {}", fileName, response.getStatusCode());
//    response.getHeaders().forEach((key, value) -> log.info("header {} with value {}", key, value));

    Arrays.stream(response).forEach(header -> log.info("header {} with value {}", header.getName(), header.getValue()));
    return AggregatesDataSummary.builder().build();
  }
}
