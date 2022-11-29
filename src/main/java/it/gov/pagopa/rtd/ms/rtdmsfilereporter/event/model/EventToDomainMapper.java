package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

public class EventToDomainMapper {

  private EventToDomainMapper() {
  }

  public static ModelMapper createEventDomainMapper() {
    ModelMapper modelMapper = new ModelMapper();

    // mapping between fields
    TypeMap<ProjectorEventDto, FileMetadata> propertyMapper = modelMapper.createTypeMap(
        ProjectorEventDto.class, FileMetadata.class);
    propertyMapper.addMapping(ProjectorEventDto::getFileName, FileMetadata::setName);
    propertyMapper.addMapping(ProjectorEventDto::getReceiveTimestamp,
        FileMetadata::setTransmissionDate);

    // mapping from event status to domain status
    Converter<EventStatusEnum, FileStatusEnum> mapStatus = context -> convertFromStringToStatusEnum(
        context.getSource());
    propertyMapper.addMappings(mapper -> mapper.using(mapStatus)
        .map(ProjectorEventDto::getStatus, FileMetadata::setStatus));

    return modelMapper;
  }

  private static FileStatusEnum convertFromStringToStatusEnum(EventStatusEnum status) {
    switch (status) {
      case RECEIVED:
        return FileStatusEnum.RECEIVED_BY_PAGOPA;
      case DECRYPTED:
        return FileStatusEnum.VALIDATED_BY_PAGOPA;
      case SENT_TO_ADE:
        return FileStatusEnum.SENT_TO_AGENZIA_DELLE_ENTRATE;
      case ACK_DOWNLOADED:
        return FileStatusEnum.ACK_DOWNLOADED;
      case ACK_TO_DOWNLOAD:
        return FileStatusEnum.ACK_TO_DOWNLOAD;
      default:
        return null;
    }
  }
}
