package it.gov.pagopa.rtd.ms.rtdmsfilereporter;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.ReportFileTTL;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class RtdMsFileReporterApplication {

  public static void main(String[] args) {
    SpringApplication.run(RtdMsFileReporterApplication.class, args);
  }

}
