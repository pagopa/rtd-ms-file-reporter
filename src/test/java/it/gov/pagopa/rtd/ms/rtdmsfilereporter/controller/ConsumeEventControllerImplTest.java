package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.FileReportEventAdapter;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventStatusEnum;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import java.time.LocalDateTime;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ConsumeEventControllerImpl.class)
class ConsumeEventControllerImplTest {

  private final String FILE_REPORT_URL = "/consume-event";

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  private FileReportEventAdapter adapter;

  @SneakyThrows
  @Test
  void givenEmptyReportWhenGetFileReportThenReturnEmptyListJson() {
    ProjectorEventDto event = new ProjectorEventDto(
        "ADE.12345.TRNLOG.20221212.113159.001.01.csv.pgp", "12345",
        987L, LocalDateTime.of(2022, 12, 19, 14, 5, 38, 543), EventStatusEnum.DECRYPTED);
    String fileEvent = mapper.writeValueAsString(event);
    mockMvc.perform(MockMvcRequestBuilders.put(FILE_REPORT_URL).content(
            (fileEvent)).contentType("application/json"))
        .andExpectAll(status().isOk())
        .andReturn();
  }

}
