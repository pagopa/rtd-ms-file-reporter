package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "StorageAccountConnector")
public interface StorageAccountRestConnector {

  @GetMapping(value = "/{container/{fileName}")
  ResponseEntity<Void> getBlobMetadata(
//      @RequestHeader("Ocp-Apim-Subscription-Key") String token,
      @PathVariable("container") String container,
      @PathVariable("fileName") String fileName);
}
