package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import java.io.IOException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "StorageAccountConnector")
public interface StorageAccountRestConnector {

//  @GetMapping(value = "{basePath}{fileName}?comp=metadata")
  ResponseEntity<Void> getBlobMetadata(
//      @RequestHeader("Ocp-Apim-Subscription-Key") String token,
//      @PathVariable("basePath")
      String basePath,
//      @PathVariable("fileName")
      String fileName) throws IOException;
}
