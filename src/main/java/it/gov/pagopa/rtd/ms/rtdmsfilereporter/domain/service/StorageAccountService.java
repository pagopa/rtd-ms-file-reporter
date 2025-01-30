package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.AggregatesDataSummaryMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.StorageAccountRestConnector;
import java.io.IOException;
import java.time.format.DateTimeParseException;
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

      log.warn("Failed to retrieve the file metadata from the storage! Filename {} Error: {}", basePath.concat(fileName), e.getMessage());
      return AggregatesDataSummary.createInvalidDataSummary();
    }

    // catch any eventual cast exceptions
    AggregatesDataSummary dataSummary;
    try {
      dataSummary = mapper.getDataSummary(response);
    } catch (DateTimeParseException | NumberFormatException ex) {
      log.error("Error in parsing some metadata! Error: {}", ex.getMessage());
      return AggregatesDataSummary.createInvalidDataSummary();
    }

    return dataSummary;
  }
}
