package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception.FileMetadataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(FileMetadataNotFoundException.class)
    protected ResponseEntity<String> handleFileMetadataNotFound(FileMetadataNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
