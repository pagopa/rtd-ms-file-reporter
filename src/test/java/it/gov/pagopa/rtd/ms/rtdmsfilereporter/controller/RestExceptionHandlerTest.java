package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception.FileMetadataNotFoundException;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.MalformedParametersException;
import java.util.NoSuchElementException;

public class RestExceptionHandlerTest {

  @Mock RestExceptionHandler restExceptionHandler;
  AutoCloseable autoCloseable;

  @BeforeEach
  void setUp() {
    autoCloseable = MockitoAnnotations.openMocks(this);
    restExceptionHandler = new RestExceptionHandler();
  }

  @SneakyThrows
  @AfterEach
  void tearDown() {
    autoCloseable.close();
  }

  @Test
  void testHandleFileMetadataNotFound() {
    FileMetadataNotFoundException exception =
        new FileMetadataNotFoundException("File metadata not found");

    ResponseEntity<String> response = restExceptionHandler.handleFileMetadataNotFound(exception);

    Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    Assertions.assertEquals("File metadata not found", response.getBody());
  }

  @Test
  void testHandleNoSuchElementFound() {
    NoSuchElementException exception = new NoSuchElementException("File metadata not found");

    ResponseEntity<String> response = restExceptionHandler.handleNoSuchElement(exception);

    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals("File metadata not found", response.getBody());
  }

  @Test
  void testHandleMalformedParameters() {
    MalformedParametersException exception =
        new MalformedParametersException("Filename  malformed");

    ResponseEntity<String> response = restExceptionHandler.handleMalformedParameters(exception);

    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals("Filename  malformed", response.getBody());
  }
}
