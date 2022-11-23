package it.gov.pagopa.rtd.ms.rtdmsfilereporter.configuration;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.repository.FileReportRepository;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportService;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.service.FileReportServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

  @Bean
  public ModelMapper getModelMapper() {
    return new ModelMapper();
  }

  @Bean
  public FileReportService getFileReportService(FileReportRepository repository) {
    return new FileReportServiceImpl(repository);
  }
}
