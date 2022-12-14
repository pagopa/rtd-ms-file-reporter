package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntity;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileReportDao extends MongoRepository<FileReportEntity, String> {

  Collection<FileReportEntity> findBySenderCodeIn(Collection<String> senderCodes);

  Optional<FileReportEntity> findBySenderCode(String senderCode);
}
