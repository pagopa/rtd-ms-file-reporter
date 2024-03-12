package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import java.time.LocalDateTime;
import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SquaringData {

  private Collection<LocalDateTime> accountingDates;
  private int numberOfMerchants;
  private long countNegativeTransactions;
  private long countPositiveTransactions;
  private long sumAmountNegativeTransactions;
  private long sumAmountPositiveTransactions;

}
