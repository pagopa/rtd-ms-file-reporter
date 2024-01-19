package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventToDomainMapper {

  @Mapping(source = "fileName", target = "name")
  @Mapping(source = "receiveTimestamp", target = "transmissionDate")
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
