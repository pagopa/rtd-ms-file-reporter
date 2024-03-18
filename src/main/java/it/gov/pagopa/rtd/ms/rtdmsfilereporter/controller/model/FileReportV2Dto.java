package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model;

import java.util.Collection;
import lombok.Data;

@Data
public class FileReportV2Dto {

  Collection<FileMetadataV2Dto> filesRecentlyUploaded;
}
