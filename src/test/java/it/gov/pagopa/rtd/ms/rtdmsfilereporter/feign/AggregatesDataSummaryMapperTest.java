package it.gov.pagopa.rtd.ms.rtdmsfilereporter.feign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.AggregatesDataSummary;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class AggregatesDataSummaryMapperTest {

  private final AggregatesDataSummaryMapper mapper = Mappers.getMapper(AggregatesDataSummaryMapper.class);

  @Test
  void whenMapThenOk() {
    var metadataMap = Map.of("numberOfMerchants", "10");

    var dataSummary = mapper.getDataSummary(metadataMap);

    assertThat(dataSummary).isNotNull()
        .extracting(AggregatesDataSummary::getCountNegativeTransactions).isEqualTo(0L);
    assertThat(dataSummary)
        .extracting(AggregatesDataSummary::getCountPositiveTransactions).isEqualTo(0L);
    assertThat(dataSummary)
        .extracting(AggregatesDataSummary::getNumberOfMerchants).isEqualTo(10);
    assertThat(dataSummary)
        .extracting(AggregatesDataSummary::getSumAmountPositiveTransactions).isEqualTo(0L);
    assertThat(dataSummary)
        .extracting(AggregatesDataSummary::getSumAmountNegativeTransactions).isEqualTo(0L);
    assertThat(dataSummary)
        .extracting(AggregatesDataSummary::getMinAccountingDate).isNull();
    assertThat(dataSummary)
        .extracting(AggregatesDataSummary::getMaxAccountingDate).isNull();
  }
}