package it.gov.pagopa.rtd.ms.rtdmsfilereporter.event;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.event.model.ProjectorEventDto;
import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

/**
 * Component defining the processing steps in response to events in the projector queue.
 */
@Configuration
public class EventHandler {

  @Bean
  public Consumer<Message<ProjectorEventDto>> projectorConsumer(FileReportEventAdapter adapter) {
    return message -> adapter.consumeEvent(message.getPayload());
  }
}
