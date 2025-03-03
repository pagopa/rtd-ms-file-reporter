package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception.FileMetadataNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.MalformedParametersException;
import java.net.MalformedURLException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(FileMetadataNotFoundException.class)
  protected ResponseEntity<String> handleFileMetadataNotFound(FileMetadataNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  protected ResponseEntity<String> handleNoSuchElement(NoSuchElementException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(MalformedParametersException.class)
  protected ResponseEntity<String> handleMalformedParameters(MalformedParametersException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

}
