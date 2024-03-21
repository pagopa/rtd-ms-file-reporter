package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.StorageAccountService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.io.IOException;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DecryptedEventCommand implements BiConsumer<FileReport, FileMetadata> {

  private final StorageAccountService client;

  @Override
  public void accept(FileReport fileReport, FileMetadata fileMetadata) {
    // preliminary api call
    AggregatesDataSummary dataSummary = null;
    try {
      dataSummary = client.getMetadata(fileMetadata.getPath(), fileMetadata.getName());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    // actions on domain object
    fileMetadata.enrichWithSquaringData(dataSummary);
    // two operations are needed: update status + add aggregates summary
    fileReport.addFileOrUpdateStatusIfPresent(fileMetadata);
    fileReport.addSquaringDataToFile(fileMetadata);
  }

}
