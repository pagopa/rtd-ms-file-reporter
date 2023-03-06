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
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class,
    MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@ActiveProfiles("kafka-test")
@EmbeddedKafka(topics = {
    "${test.kafka.topic}"}, partitions = 1, bootstrapServersProperty = "spring.embedded.kafka.brokers")
@Import(KafkaTestConfiguration.class)
class EventHandlerTest {

  @Value("${test.kafka.topic}")
  private String topic;
  @Autowired
  private StreamBridge stream;
  @Autowired
  private EmbeddedKafkaBroker broker;
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

    stream.send("projectorConsumer-in-0", MessageBuilder.withPayload(eventDto).build());

    verify(projectorConsumer, times(1)).accept(any());
  }

  @SneakyThrows
  @Test
  void whenAMalformedEventIsSentThenRaiseException() {
    var currentDate = LocalDateTime.now();
    String eventDtoJson =
        "{\"fileName\": \"prova\", \"sender\": \"Sender\", \"size\":100, \"receiveTimestamp\": \"" +
            currentDate + "\", \"status\": \"UNMAPPED\"}";

    assertThatThrownBy(() -> objectMapper.readValue(eventDtoJson, ProjectorEventDto.class))
        .isInstanceOf(InvalidFormatException.class);
  }

}
