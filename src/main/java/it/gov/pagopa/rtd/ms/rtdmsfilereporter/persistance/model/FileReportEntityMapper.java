package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.Collection;
import java.util.Collections;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

public class FileReportEntityMapper {

  private FileReportEntityMapper() {
  }

  public static ModelMapper createEntityDomainMapper() {
    ModelMapper modelMapper = new ModelMapper();
    // add custom conversion from list of sender codes to String and viceversa
    Converter<String, Collection<String>> stringToCollection = c -> Collections.singleton(
        c.getSource());
    Converter<Collection<String>, String> collectionToString = c -> c.getSource().stream()
        .findAny().orElse(null);

    var propertyMapperEntityToDomain = modelMapper.createTypeMap(
        FileReportEntity.class, FileReport.class);
    propertyMapperEntityToDomain.addMappings(mapper -> mapper.using(stringToCollection)
        .map(FileReportEntity::getSenderCode, FileReport::setSenderCodes));

    var propertyMapperDomainToEntity = modelMapper.createTypeMap(FileReport.class,
        FileReportEntity.class);
    propertyMapperDomainToEntity.addMappings(mapper -> mapper.using(collectionToString)
        .map(FileReport::getSenderCodes, FileReportEntity::setSenderCode));

    return modelMapper;
  }

}
