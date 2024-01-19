package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface FileReportDtoMapper {

  FileReportDtoMapper INSTANCE = Mappers.getMapper(FileReportDtoMapper.class);

  @Mapping(source = "filesUploaded", target = "filesRecentlyUploaded")
  FileReportDto fileReportToDto(FileReport fileReport);
}
