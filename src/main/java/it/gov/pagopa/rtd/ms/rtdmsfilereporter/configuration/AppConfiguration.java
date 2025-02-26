package it.gov.pagopa.rtd.ms.rtdmsfilereporter.configuration;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.FileReportCommandFactory;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.DecryptedEventCommand;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportServiceImpl;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.StorageAccountService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.ReportConfiguration;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.FileReportDao;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.FileReportRepositoryImpl;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntityMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to define common beans and domain bean. In this way the service class inside
 * the domain are not bounded to the specific framework.
 */
@Configuration
@EnableConfigurationProperties(ReportConfiguration.class)
public class AppConfiguration {

  @Bean
  public FileReportService getFileReportService(FileReportRepository repository, StorageAccountService service, ReportConfiguration report) {
    return new FileReportServiceImpl(repository, service, report);
  }

  @Bean
  public FileReportRepository getFileReportRepository(FileReportDao dao,
      FileReportEntityMapper mapper) {
    return new FileReportRepositoryImpl(dao, mapper);
  }

  @Bean
  public FileReportCommandFactory getFileReportCommandFactory(DecryptedEventCommand command) {
    return new FileReportCommandFactory(command);
  }
}
