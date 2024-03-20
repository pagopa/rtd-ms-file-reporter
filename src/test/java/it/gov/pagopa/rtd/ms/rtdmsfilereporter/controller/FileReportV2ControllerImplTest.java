package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportV2DtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(FileReportV2ControllerImpl.class)
class FileReportV2ControllerImplTest {

  private final String FILE_REPORT_URL_V2 = "/v2/file-report";
  ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private FileReportV2DtoMapper mapperV2;
  @MockBean
  private FileReportService fileReportService;

  @SneakyThrows
  @Test
  void givenReportWhenGetFileReportV2ThenReturnCorrectJson() {
    var reportMock = TestUtils.createFileReport(0, 1);
    var reportDto = Mappers.getMapper(FileReportV2DtoMapper.class).fileReportToDto(reportMock);
    reportDto.getFilesRecentlyUploaded().stream()
        .findAny()
        .ifPresent(file -> file.setDataSummary(
            AggregatesDataSummary.builder()
                .countPositiveTransactions(10)
                .sumAmountPositiveTransactions(1000)
                .build()));
    Mockito.when(mapperV2.fileReportToDto(any())).thenReturn(reportDto);

    MvcResult result = mockMvc.perform(
            MockMvcRequestBuilders.get(FILE_REPORT_URL_V2).param("senderCodes", "12345"))
        .andExpectAll(status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andReturn();
    it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportDto fileReportResponse = objectMapper.readValue(
        result.getResponse().getContentAsString(),
        it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportDto.class);

    assertThat(fileReportResponse.getFilesRecentlyUploaded()).isNotNull().hasSize(1);
    var file = fileReportResponse.getFilesRecentlyUploaded().stream().findAny();
    assertThat(file).isPresent();
    var summary = file.get().getDataSummary();
    assertThat(summary).isNotNull();
    assertThat(summary.getCountPositiveTransactions()).isEqualTo(10);
    assertThat(summary.getSumAmountPositiveTransactions()).isEqualTo(1000);
  }

  @SneakyThrows
  @Test
  void givenNoQueryParamsWhenGetFileReportThenReturn400() {
    Mockito.when(fileReportService.getAggregateFileReport(any()))
        .thenReturn(FileReport.createFileReport());

    mockMvc.perform(MockMvcRequestBuilders.get(FILE_REPORT_URL_V2))
        .andExpect(status().isBadRequest())
        .andReturn();
  }
}