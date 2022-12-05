package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

public class FileReportDtoMapper {

  private FileReportDtoMapper() {
  }

  public static ModelMapper createDtoDomainMapper() {
    ModelMapper modelMapper = new ModelMapper();
    // add custom conversion from filesUploaded to filesRecentlyUploaded
    TypeMap<FileReport, FileReportDto> propertyMapper = modelMapper.createTypeMap(FileReport.class,
        FileReportDto.class);
    propertyMapper.addMapping(FileReport::getFilesUploaded,
        FileReportDto::setFilesRecentlyUploaded);

    return modelMapper;
  }

}
