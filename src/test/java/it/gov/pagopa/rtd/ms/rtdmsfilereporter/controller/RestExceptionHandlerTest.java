package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.exception.FileMetadataNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

class RestExceptionHandlerTest {

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
  void testHandleInputMismatch() {
    InputMismatchException exception =
        new InputMismatchException("Filename  malformed");

    ResponseEntity<String> response = restExceptionHandler.handleInputMismatch(exception);

    Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assertions.assertEquals("Filename  malformed", response.getBody());
  }
}
