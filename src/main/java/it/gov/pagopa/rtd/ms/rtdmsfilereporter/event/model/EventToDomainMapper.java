package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface EventToDomainMapper {


  /**
   * Extract the file name from a path. e.g. '/container/filename.txt' returns 'filename.txt'
   */
  @Named("filePathToFileName")
  static String filePathToFileName(String filePath) {
    return filePath.substring(filePath.lastIndexOf('/') + 1);
  }

  /**
   * Map from a path containing the file name to a string containing the path only.
   * e.g. '/container/filename.txt' returns '/container/'
   */
  @Named("filePathToPath")
  static String filePathToPath(String filePath) {
    if (filePath.contains("/")) {
      return filePath.substring(0, filePath.lastIndexOf('/') + 1);
    } else {
      return "";
    }
  }

  @Mapping(source = "filePath", target = "name", qualifiedByName = "filePathToFileName")
  @Mapping(source = "filePath", target = "path", qualifiedByName = "filePathToPath")
  @Mapping(source = "receiveTimestamp", target = "transmissionDate")
  @Mapping(target = "aggregatesDataSummary", ignore = true)
  FileMetadata eventToDomain(ProjectorEventDto eventDto);

  default FileStatusEnum convertFromStringToStatusEnum(EventStatusEnum status) {
    return switch (status) {
      case RECEIVED -> FileStatusEnum.RECEIVED_BY_PAGOPA;
      case DECRYPTED -> FileStatusEnum.VALIDATED_BY_PAGOPA;
      case SENT_TO_ADE -> FileStatusEnum.SENT_TO_AGENZIA_DELLE_ENTRATE;
      case ACK_DOWNLOADED -> FileStatusEnum.ACK_DOWNLOADED;
      case ACK_TO_DOWNLOAD -> FileStatusEnum.ACK_TO_DOWNLOAD;
    };
  }
}
