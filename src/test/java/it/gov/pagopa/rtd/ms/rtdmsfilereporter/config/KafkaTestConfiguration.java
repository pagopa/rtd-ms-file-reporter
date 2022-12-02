package it.gov.pagopa.rtd.ms.rtdmsfilereporter.config;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.FileReportDao;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class KafkaTestConfiguration {

  @MockBean
  private FileReportDao dao;
}
