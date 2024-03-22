package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AggregatesDataSummaryMapper {

  @Mapping(source = "numMerchant", target = "numberOfMerchants")
  @Mapping(source = "numCanceledTrx", target = "countNegativeTransactions")
  @Mapping(source = "numPositiveTrx", target = "countPositiveTransactions")
  @Mapping(source = "totalAmountCanceledTrx", target = "sumAmountNegativeTransactions")
  @Mapping(source = "totalAmountPositiveTrx", target = "sumAmountPositiveTransactions")
  @Mapping(source = "maxAccountingDate", target = "maxAccountingDate")
  @Mapping(source = "minAccountingDate", target = "minAccountingDate")
  @Mapping(source = "checkSum", target = "sha256OriginFile")
  AggregatesDataSummary getDataSummary(Map<String, String> data);
}
