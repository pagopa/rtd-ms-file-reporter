package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2;

import java.util.Collection;
import lombok.Data;

@Data
public class FileReportDto {

  Collection<FileMetadataDto> filesRecentlyUploaded;
}
