package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventStatusEnum;
import java.util.function.BiConsumer;

public class FileReportStrategy {

  public BiConsumer<FileReport, FileMetadata> getCommandByStatus(String status) {
    switch (EventStatusEnum.valueOf(status)) {
      case ACK_TO_DOWNLOAD:
        return (fileReport, fileMetadata) -> fileReport.addAckToDownload(fileMetadata.getName());
      case ACK_DOWNLOADED:
        return (fileReport, fileMetadata) -> fileReport.removeAckToDownload(fileMetadata.getName());
      default:
        return FileReport::addFileOrUpdateStatusIfPresent;
    }
  }
}
