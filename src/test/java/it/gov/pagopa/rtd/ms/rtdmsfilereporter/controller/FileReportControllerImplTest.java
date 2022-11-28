package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileMetadataDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileReportDtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(FileReportControllerImpl.class)
class FileReportControllerImplTest {

  private final String FILE_REPORT_URL = "/file-report";

  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  private final ModelMapper modelMapper = FileReportDtoMapper.createDtoDomainMapper();

  @MockBean
  private FileReportService fileReportService;

  @SneakyThrows
  @Test
  void givenEmptyReportWhenGetFileReportThenReturnEmptyListJson() {
    Mockito.when(fileReportService.getAggregateFileReport(any()))
        .thenReturn(FileReport.createFileReport());

    FileReportDto fileReportDto = new FileReportDto();
    fileReportDto.setFilesRecentlyUploaded(new HashSet<>());
    String emptyFileReportAsJson = objectMapper.writeValueAsString(fileReportDto);

    mockMvc.perform(MockMvcRequestBuilders.get(FILE_REPORT_URL).param("senderCodes", "12345"))
        .andExpectAll(status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON_VALUE),
            content().string(emptyFileReportAsJson))
        .andReturn();

  }

  @SneakyThrows
  @Test
  void givenReportWhenGetFileReportThenReturnCorrectJson() {
    Mockito.when(fileReportService.getAggregateFileReport(any()))
        .thenReturn(TestUtils.createFileReport(2, 2));

    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(FILE_REPORT_URL).param("senderCodes", "12345"))
        .andExpectAll(status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andReturn();
    FileReportDto fileReportResponse = objectMapper.readValue(
        result.getResponse().getContentAsString(), FileReportDto.class);

    assertThat(fileReportResponse.getFilesRecentlyUploaded()).isNotNull().hasSize(2);
  }

  @SneakyThrows
  @Test
  void givenNoQueryParamsWhenGetFileReportThenReturn400() {
    Mockito.when(fileReportService.getAggregateFileReport(any()))
        .thenReturn(FileReport.createFileReport());

    mockMvc.perform(MockMvcRequestBuilders.get(FILE_REPORT_URL))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void mappingFromDomainToDtoWorksCorrectly() {
    var currentDate = LocalDateTime.now();
    FileReport fileReport = FileReport.createFileReport();
    FileMetadata fileMetadata = new FileMetadata("file", 3000L, "STATUS", currentDate);
    fileReport.addFileUploaded(fileMetadata);
    fileReport.addAckToDownload("ack1");
    fileReport.setSenderCodes(List.of("senderCode"));

    var fileReportDto = modelMapper.map(fileReport, FileReportDto.class);

    assertThat(fileReportDto).isNotNull();
    assertThat(fileReportDto.getFilesRecentlyUploaded()).isNotNull().hasSize(1)
        .extracting(FileMetadataDto::getName,
            FileMetadataDto::getSize,
            FileMetadataDto::getStatus,
            FileMetadataDto::getTransmissionDate)
        .doesNotContainNull()
        .containsExactly(Tuple.tuple("file", 3000L, "STATUS", currentDate));
  }
}
