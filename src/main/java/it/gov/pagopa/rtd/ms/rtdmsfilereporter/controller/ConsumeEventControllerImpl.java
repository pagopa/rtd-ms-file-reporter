package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.FileReportEventAdapter;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseBody
@Slf4j
public class ConsumeEventControllerImpl implements ConsumeEventController {

  @Autowired
  private FileReportEventAdapter adapter;

  @Override
  public void consumeEvent(@RequestBody @Valid ProjectorEventDto eventData) {
    log.info("Received event [{}]", eventData.getFileName());

    eventData.setReceiveTimestamp(getLocalDateTime());

    adapter.consumeEvent(MessageBuilder.withPayload(eventData).build().getPayload());
  }

  private LocalDateTime getLocalDateTime() {
    OffsetDateTime off = OffsetDateTime.parse(Instant.now().toString());
    ZonedDateTime zoned = off.atZoneSameInstant(ZoneId.of("Europe/Rome"));
    return zoned.toLocalDateTime();
  }

}
