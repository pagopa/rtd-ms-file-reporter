package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileReportV2DtoMapper {

  @Mapping(source = "filesUploaded", target = "filesRecentlyUploaded")
  FileReportV2Dto fileReportToDto(FileReport fileReport);

  @Mapping(source = "aggregatesDataSummary", target = "dataSummary")
  FileMetadataV2Dto fileMetadataToDto(FileMetadata fileMetadata);
}
