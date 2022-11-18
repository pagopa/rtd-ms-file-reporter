package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import java.util.Collection;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("filereports")
@Data
public class FileReportEntity {

  @Id
  private String id;

  @Indexed(unique = true)
  @Field(name = "senderCode")
  private String senderCode;

  @Field(name = "filesUploaded")
  private Collection<FileMetadata> filesUploaded;

  @Field(name = "ackToDownload")
  private Collection<String> ackToDownload;
}
