package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import java.util.Collection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileReportDao extends MongoRepository<FileReportEntity, String> {

  Collection<FileReportEntity> findBySenderCodeIn(Collection<String> senderCodes);
}
