package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.config.KafkaTestConfiguration;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.EventStatusEnum;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("kafka-test")
@Import({TestChannelBinderConfiguration.class, KafkaTestConfiguration.class})
class EventHandlerTest {

  @Value("${test.kafka.topic}")
  private String inputTopic;
  @Autowired
  private InputDestination inputDestination;
  @MockBean
  private Consumer<Message<ProjectorEventDto>> projectorConsumer;
  ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @SneakyThrows
  @Test
  void whenACorrectEventIsSentThenConsumeIt() {
    var currentDate = LocalDateTime.now();
    var eventDto = new ProjectorEventDto("prova", "Sender", 100L, currentDate,
        EventStatusEnum.RECEIVED);

    inputDestination.send(MessageBuilder.withPayload(eventDto).build(), inputTopic);

    verify(projectorConsumer, times(1)).accept(any());
  }

  @SneakyThrows
  @Test
  void whenAMalformedEventIsSentThenRaiseException() {
    var currentDate = LocalDateTime.now();
    String eventDtoJson =
        "{\"filePath\": \"prova\", \"sender\": \"Sender\", \"size\":100, \"receiveTimestamp\": \"" +
            currentDate + "\", \"status\": \"UNMAPPED\"}";

    assertThatThrownBy(() -> objectMapper.readValue(eventDtoJson, ProjectorEventDto.class))
        .isInstanceOf(InvalidFormatException.class);
  }

}
