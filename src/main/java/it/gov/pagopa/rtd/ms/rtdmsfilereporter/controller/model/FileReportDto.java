package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model;

import java.util.Collection;
import lombok.Data;

@Data
public class FileReportDto {

  Collection<FileMetadataDto> filesUploaded;
}
