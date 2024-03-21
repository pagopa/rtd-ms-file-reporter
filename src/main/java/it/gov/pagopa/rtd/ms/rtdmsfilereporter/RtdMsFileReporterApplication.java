package it.gov.pagopa.rtd.ms.rtdmsfilereporter;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.StorageAccountRestConnector;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.config.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@EnableFeignClients(clients = StorageAccountRestConnector.class)
@EnableConfigurationProperties(StorageProperties.class)
public class RtdMsFileReporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtdMsFileReporterApplication.class, args);
	}

}
