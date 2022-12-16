package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.FileReportEventAdapter;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
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

    adapter.consumeEvent(MessageBuilder.withPayload(eventData).build().getPayload());
  }
}
