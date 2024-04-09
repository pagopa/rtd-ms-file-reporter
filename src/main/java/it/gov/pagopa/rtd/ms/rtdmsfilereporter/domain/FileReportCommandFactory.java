package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.DecryptedEventCommand;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventStatusEnum;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileReportCommandFactory {

  private final DecryptedEventCommand decryptCommand;

  public BiConsumer<FileReport, FileMetadata> getCommandByStatus(String status) {
    return switch (EventStatusEnum.valueOf(status)) {
      case ACK_TO_DOWNLOAD ->
          (fileReport, fileMetadata) -> fileReport.addAckToDownload(fileMetadata.getName());
      case ACK_DOWNLOADED ->
          (fileReport, fileMetadata) -> fileReport.removeAckToDownload(fileMetadata.getName());
      case DECRYPTED -> decryptCommand;
      case RECEIVED, SENT_TO_ADE -> FileReport::addFileOrUpdateStatusIfPresent;
    };
  }
}
