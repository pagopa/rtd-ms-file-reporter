package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AggregatesDataSummaryMapper {

  @Mapping(source= "", target = "countNegativeTransactions")
  @Mapping(source= "", target = "countPositiveTransactions")
  @Mapping(source= "", target = "sumAmountNegativeTransactions")
  @Mapping(source= "", target = "sumAmountPositiveTransactions")
  @Mapping(source= "", target = "maxAccountingDate")
  @Mapping(source= "", target = "minAccountingDate")
  AggregatesDataSummary getDataSummary(Map<String, String> data);
}
