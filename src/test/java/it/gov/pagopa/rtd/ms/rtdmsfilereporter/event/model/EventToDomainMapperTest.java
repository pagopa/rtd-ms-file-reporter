package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model;

import static org.assertj.core.api.Assertions.assertThat;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;

class EventToDomainMapperTest {

  private final EventToDomainMapper mapper = Mappers.getMapper(EventToDomainMapper.class);

  private static Stream<Arguments> provideStatus() {
    return Stream.of(
        Arguments.of(EventStatusEnum.RECEIVED, FileStatusEnum.RECEIVED_BY_PAGOPA),
        Arguments.of(EventStatusEnum.DECRYPTED, FileStatusEnum.VALIDATED_BY_PAGOPA),
        Arguments.of(EventStatusEnum.SENT_TO_ADE, FileStatusEnum.SENT_TO_AGENZIA_DELLE_ENTRATE),
        Arguments.of(EventStatusEnum.ACK_TO_DOWNLOAD, FileStatusEnum.ACK_TO_DOWNLOAD),
        Arguments.of(EventStatusEnum.ACK_DOWNLOADED, FileStatusEnum.ACK_DOWNLOADED)
    );
  }

  private static Stream<Arguments> provideFilePath() {
    return Stream.of(
        Arguments.of("fileWithoutPath.txt", "", "fileWithoutPath.txt"),
        Arguments.of("storage/container/fileName.txt", "storage/container/", "fileName.txt"),
        Arguments.of("/fileName.txt", "/", "fileName.txt")
    );
  }

  @ParameterizedTest
  @MethodSource("provideStatus")
  void mappingFromEventToDomainWorksCorrectly(EventStatusEnum eventStatus,
      FileStatusEnum domainStatus) {
    var currentDate = LocalDateTime.now();
    var eventDto = new ProjectorEventDto("filename", "12345", 100L, currentDate,
        eventStatus);

    var domainFile = mapper.eventToDomain(eventDto);

    assertThat(domainFile).isNotNull();
    assertThat(domainFile.getStatus()).isEqualTo(domainStatus);
    assertThat(domainFile.getSize()).isEqualTo(100L);
    assertThat(domainFile.getTransmissionDate()).isEqualTo(currentDate);
  }

  @ParameterizedTest
  @MethodSource("provideFilePath")
  void mappingPathToTwoDifferentFields(String filePath, String expectedPath, String expectedName) {
    var eventDto = new ProjectorEventDto(filePath, "12345", 100L, null,
        EventStatusEnum.RECEIVED);

    var domainFile = mapper.eventToDomain(eventDto);

    assertThat(domainFile).isNotNull();
    assertThat(domainFile.getName()).isEqualTo(expectedName);
    assertThat(domainFile.getPath()).isEqualTo(expectedPath);
  }
}