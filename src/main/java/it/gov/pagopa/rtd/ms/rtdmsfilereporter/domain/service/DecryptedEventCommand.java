package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.StorageAccountClient;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DecryptedEventCommand implements BiConsumer<FileReport, FileMetadata> {

  private final StorageAccountClient client;

  @Override
  public void accept(FileReport fileReport, FileMetadata fileMetadata) {
    // preliminary api call
    var dataSummary = client.getMetadata(fileMetadata.getCompletePath());
    // actions on domain object
    fileMetadata.enrichWithSquaringData(dataSummary);
    // two operations are needed: update status + add aggregates summary
    fileReport.addFileOrUpdateStatusIfPresent(fileMetadata);
    fileReport.addSquaringDataToFile(fileMetadata);
  }

}
