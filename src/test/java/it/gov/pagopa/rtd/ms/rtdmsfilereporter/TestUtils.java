package it.gov.pagopa.rtd.ms.rtdmsfilereporter;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.stream.IntStream;

public class TestUtils {

  public static FileReport createFileReport(int numAcks, int numFiles) {
    FileReport fileReport = FileReport.createFileReport();

    IntStream.range(0, numAcks)
        .forEach(i -> fileReport.addAckToDownload("ackreport" + generateRandomNumber()));
    IntStream.range(0, numFiles)
        .forEach(i -> fileReport.addFileUploaded(
            FileMetadata.createNewFileMetadata("file" + generateRandomNumber())));

    return fileReport;
  }

  static Long generateRandomNumber() {
    try {
      return SecureRandom.getInstanceStrong().nextLong();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
