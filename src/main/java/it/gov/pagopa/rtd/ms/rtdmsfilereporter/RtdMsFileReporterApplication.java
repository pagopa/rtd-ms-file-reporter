package it.gov.pagopa.rtd.ms.rtdmsfilereporter;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign.StorageAccountRestConnector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(clients = StorageAccountRestConnector.class)
public class RtdMsFileReporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(RtdMsFileReporterApplication.class, args);
	}

}
