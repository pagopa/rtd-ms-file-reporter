package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileReportDtoMapper {

  @Mapping(source = "filesUploaded", target = "filesRecentlyUploaded")
  FileReportDto fileReportToDto(FileReport fileReport);
}
