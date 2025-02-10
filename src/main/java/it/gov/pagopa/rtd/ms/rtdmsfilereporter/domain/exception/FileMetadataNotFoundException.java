package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception;

public class FileMetadataNotFoundException extends RuntimeException {
    public FileMetadataNotFoundException(String message) {
        super(message);
    }
}
