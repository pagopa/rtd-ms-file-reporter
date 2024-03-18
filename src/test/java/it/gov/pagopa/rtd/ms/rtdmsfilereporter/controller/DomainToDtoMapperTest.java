package it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller;

import static org.assertj.core.api.Assertions.assertThat;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileMetadataDto;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.controller.model.FileReportDtoMapper;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileMetadata;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileReport;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model.FileStatusEnum;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class DomainToDtoMapperTest {

  private final FileReportDtoMapper mapper = Mappers.getMapper(FileReportDtoMapper.class);

  @Test
  void mappingFromDomainToDtoWorksCorrectly() {
    var currentDate = LocalDateTime.now();
    FileReport fileReport = FileReport.createFileReport();
    FileMetadata fileMetadata = FileMetadata.builder().name("file").size(3000L)
        .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
        .transmissionDate(currentDate).build();
    fileReport.addFileUploaded(fileMetadata);
    fileReport.addAckToDownload("ack1");
    fileReport.setSenderCodes(List.of("senderCode"));

    var fileReportDto = mapper.fileReportToDto(fileReport);

    assertThat(fileReportDto).isNotNull();
    assertThat(fileReportDto.getFilesRecentlyUploaded()).isNotNull().hasSize(1)
        .extracting(FileMetadataDto::getName,
            FileMetadataDto::getSize,
            FileMetadataDto::getStatus,
            FileMetadataDto::getTransmissionDate)
        .doesNotContainNull()
        .containsExactly(
            Tuple.tuple("file", 3000L, FileStatusEnum.RECEIVED_BY_PAGOPA.name(), currentDate));
  }


}
