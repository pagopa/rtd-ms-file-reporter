package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AggregatesDataSummary {

  private LocalDateTime minAccountingDate;
  private LocalDateTime maxAccountingDate;
  private int numberOfMerchants;
  private long countNegativeTransactions;
  private long countPositiveTransactions;
  private long sumAmountNegativeTransactions;
  private long sumAmountPositiveTransactions;
  // sha256 of the initial input file containing the transactions
  private String sha256OriginFile;
}
