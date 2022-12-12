package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

public enum FileStatusEnum {
  RECEIVED_BY_PAGOPA,
  VALIDATED_BY_PAGOPA,
  SENT_TO_AGENZIA_DELLE_ENTRATE,
  ACK_TO_DOWNLOAD,
  ACK_DOWNLOADED
}
