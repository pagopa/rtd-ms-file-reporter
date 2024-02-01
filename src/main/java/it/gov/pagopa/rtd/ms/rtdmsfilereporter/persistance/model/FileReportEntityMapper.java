package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileReportEntityMapper {

  @Mapping(source = "senderCode", target = "senderCodes")
  FileReport entityToDomain(FileReportEntity entity);

  @Mapping(source = "senderCodes", target = "senderCode")
  FileReportEntity domainToEntity(FileReport domain);

  default String mapListToString(Collection<String> relation) {
    return relation.stream().findAny().orElse(null);
  }

  default Collection<String> mapStringToList(String value) {
    return value == null ? Collections.emptySet() : Set.of(value);
  }
}
