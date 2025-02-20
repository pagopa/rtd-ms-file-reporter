package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1.FileReportDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v1.FileReportDtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportV2Dto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.v2.FileReportV2DtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import java.util.Collections;
import java.util.HashSet;
import lombok.SneakyThrows;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(FileReportControllerImpl.class)
class FileReportControllerImplTest {

  private final String FILE_REPORT_URL = "/file-report";
    private final String FILE_REPORT_URL_V2 = "/v2/file-report";
  ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  private MockMvc mockMvc;

  private FileReportDtoMapper mapper;

  private FileReportV2DtoMapper mapperV2;

  private FileReportService fileReportService;

  @SneakyThrows
  @Test
  void givenEmptyReportWhenGetFileReportThenReturnEmptyListJson() {
    var emptyReportDto = new FileReportDto();
    emptyReportDto.setFilesRecentlyUploaded(Collections.emptyList());
    Mockito.when(mapper.fileReportToDto(any())).thenReturn(emptyReportDto);

    FileReportDto fileReportDto = new FileReportDto();
    fileReportDto.setFilesRecentlyUploaded(new HashSet<>());
    String emptyFileReportAsJson = objectMapper.writeValueAsString(fileReportDto);

    mockMvc
        .perform(MockMvcRequestBuilders.get(FILE_REPORT_URL).param("senderCodes", "12345"))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON_VALUE),
            content().string(emptyFileReportAsJson))
        .andReturn();
  }

  @SneakyThrows
  @Test
  void givenReportWhenGetFileReportThenReturnCorrectJson() {
    var reportMock = TestUtils.createFileReport(2, 2);
    var reportDto = Mappers.getMapper(FileReportDtoMapper.class).fileReportToDto(reportMock);
    Mockito.when(mapper.fileReportToDto(any())).thenReturn(reportDto);

    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get(FILE_REPORT_URL).param("senderCodes", "12345"))
            .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    FileReportDto fileReportResponse =
        objectMapper.readValue(result.getResponse().getContentAsString(), FileReportDto.class);

    assertThat(fileReportResponse.getFilesRecentlyUploaded()).isNotNull().hasSize(2);
  }

  @SneakyThrows
  @Test
  void givenNoQueryParamsWhenGetFileReportThenReturn400() {
    Mockito.when(fileReportService.getAggregateFileReport(any()))
        .thenReturn(FileReport.createFileReport());

    mockMvc
        .perform(MockMvcRequestBuilders.get(FILE_REPORT_URL))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void whenGetAdeAckToDownloadThenReturns200AndCorrectBody() throws Exception {
    String senderAdeAckFileName1 =
        "ADEACK.99999.12345.2022-09-13.709f29ed-2a34-4c73-9a23-397e2e768ecf.csv";
    String senderAdeAckFileName2 =
        "ADEACK.11111.12345.2022-09-13.709f29ed-2a34-4c73-9a23-397e2e768ecf.csv";
    Mockito.when(fileReportService.getAckToDownloadList(any()))
        .thenReturn(Sets.set(senderAdeAckFileName1, senderAdeAckFileName2));

      String SENDER_ADE_ACK_URL = "/sender-ade-ack";
      MvcResult result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(SENDER_ADE_ACK_URL)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .param("senderCodes", "99999", "11111")
                    .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

    assertEquals(
        "{\"fileNameList\":[\"" + senderAdeAckFileName1 + "\",\"" + senderAdeAckFileName2 + "\"]}",
        result.getResponse().getContentAsString());

    BDDMockito.verify(fileReportService, Mockito.times(1))
        .getAckToDownloadList(ArgumentMatchers.anyCollection());
  }

  @SneakyThrows
  @Test
  void givenReportWhenGetFileReportV2ThenReturnCorrectJson() {
    var reportMock = TestUtils.createFileReport(0, 1);
    var reportDto = Mappers.getMapper(FileReportV2DtoMapper.class).fileReportToDto(reportMock);
    reportDto.getFilesRecentlyUploaded().stream()
        .findAny()
        .ifPresent(
            file ->
                file.setDataSummary(
                    AggregatesDataSummary.builder()
                        .countPositiveTransactions(10)
                        .sumAmountPositiveTransactions(1000)
                        .build()));
    Mockito.when(mapperV2.fileReportToDto(any())).thenReturn(reportDto);

    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get(FILE_REPORT_URL_V2).param("senderCodes", "12345"))
            .andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andReturn();
    FileReportV2Dto fileReportResponse =
        objectMapper.readValue(result.getResponse().getContentAsString(), FileReportV2Dto.class);

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
  void givenNoQueryParamsWhenGetFileReportV2ThenReturn400() {
    Mockito.when(fileReportService.getAggregateFileReport(any()))
        .thenReturn(FileReport.createFileReport());

    mockMvc
        .perform(MockMvcRequestBuilders.get(FILE_REPORT_URL_V2))
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @SneakyThrows
  @Test
  void givenValidFilenameWhenGetMetadataEndpointThenStatusOkAndServiceCalled() {
    String basePath = "myBasePath";
    String fileName = "ADE.12345.TRNLOG.20230101.130000.001.01.csv.pgp";

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/report/metadata")
                .param("basePath", basePath)
                .param("fileName", fileName))
        .andExpect(status().isOk());

    Mockito.verify(fileReportService).getMetadata(basePath, fileName);
  }
}
