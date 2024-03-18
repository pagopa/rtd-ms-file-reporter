package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;

class FileReportTest {


  @Test
  void whenCreateEmptyReportThenFieldsAreInitiatedCorrectly() {
    FileReport fileReport = FileReport.createFileReport();

    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();
    assertThat(fileReport.getAckToDownload()).isNotNull().isEmpty();
    assertThat(fileReport.getId()).isNull();
    assertThat(fileReport.getSenderCodes()).isNotNull().isEmpty();
  }

  @Test
  void whenAddFileUploadedThenCollectionIsUpdatedAndSortedCorrectly() {
    FileReport fileReport = FileReport.createFileReport();

    fileReport.addFileUploaded(
        FileMetadata.builder().name("oldestFile").size(200L)
            .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(LocalDateTime.of(2022, 1, 1, 10, 0)).build());
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1)
        .map(FileMetadata::getName).contains("oldestFile");

    fileReport.addFileUploaded(
        FileMetadata.builder().name("mostRecentFile").size(200L)
            .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(LocalDateTime.of(2022, 1, 2, 10, 0)).build());
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2)
        .map(FileMetadata::getName).containsExactly("mostRecentFile", "oldestFile");
  }

  @Test
  void whenAddFileUploadedThenCollectionIsUpdatedAndSortedCorrectlyWithSameTimestamp() {
    FileReport fileReport = FileReport.createFileReport();

    fileReport.addFileUploaded(
        FileMetadata.builder().name("fileA").size(200L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(LocalDateTime.of(2022, 1, 1, 10, 0)).build());
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1)
        .map(FileMetadata::getName).contains("fileA");

    fileReport.addFileUploaded(
        FileMetadata.builder().name("fileB").size(200L).status(FileStatusEnum.RECEIVED_BY_PAGOPA)
            .transmissionDate(LocalDateTime.of(2022, 1, 1, 10, 0)).build());
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2)
        .map(FileMetadata::getName).containsExactly("fileB", "fileA");
  }

  @Test
  void givenOneFileUploadedWhenRemoveFileUploadedThenCollectionIsEmpty() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file1"));

    fileReport.removeFileUploaded("file1");

    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();
  }

  @Test
  void givenFilesUploadedWhenRemoveFileUploadedThenCollectionIsUpdatedCorrectly() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file1"));
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file2"));

    fileReport.removeFileUploaded("file1");

    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1).map(FileMetadata::getName)
        .contains("file2");
  }

  @Test
  void givenFilesUploadedWhenRemoveWrongFileUploadedThenNothingHappens() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file1"));
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file2"));

    fileReport.removeFileUploaded("wrongname");

    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2).map(FileMetadata::getName)
        .contains("file1", "file2");
  }

  @Test
  void whenUpdateFileStatusThenCollectionIsUpdatedCorrectly() {
    FileReport fileReport = FileReport.createFileReport();
    var file = FileMetadata.createNewFileMetadataWithStatus("file1",
        FileStatusEnum.RECEIVED_BY_PAGOPA);
    var file2 = FileMetadata.createNewFileMetadataWithStatus("file2",
        FileStatusEnum.VALIDATED_BY_PAGOPA);
    fileReport.addFileUploaded(file);
    fileReport.addFileUploaded(file2);

    assertThat(fileReport.getFilesUploaded()).map(FileMetadata::getName, FileMetadata::getStatus)
        .contains(Tuple.tuple("file1", FileStatusEnum.RECEIVED_BY_PAGOPA),
            Tuple.tuple("file2", FileStatusEnum.VALIDATED_BY_PAGOPA));

    fileReport.addFileOrUpdateStatusIfPresent(
        FileMetadata.createNewFileMetadataWithStatus("file1",
            FileStatusEnum.SENT_TO_AGENZIA_DELLE_ENTRATE));

    assertThat(fileReport.getFilesUploaded()).map(FileMetadata::getName, FileMetadata::getStatus)
        .contains(Tuple.tuple("file1", FileStatusEnum.SENT_TO_AGENZIA_DELLE_ENTRATE),
            Tuple.tuple("file2", FileStatusEnum.VALIDATED_BY_PAGOPA));
  }

  @Test
  void givenEmptyFilesListWhenUpdateFileStatusThenNewFileIsAdded() {
    FileReport fileReport = FileReport.createFileReport();
    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();

    fileReport.addFileOrUpdateStatusIfPresent(
        FileMetadata.createNewFileMetadataWithStatus("file1", FileStatusEnum.RECEIVED_BY_PAGOPA));

    assertThat(fileReport.getFilesUploaded()).map(FileMetadata::getName, FileMetadata::getStatus)
        .contains(Tuple.tuple("file1", FileStatusEnum.RECEIVED_BY_PAGOPA));
  }

  @Test
  void whenAddAckThenCollectionIsUpdatedCorrectly() {
    FileReport fileReport = FileReport.createFileReport();

    fileReport.addAckToDownload("adeack");
    assertThat(fileReport.getAckToDownload()).isNotNull().hasSize(1).contains("adeack");

    fileReport.addAckToDownload("adeack2");
    assertThat(fileReport.getAckToDownload()).isNotNull().hasSize(2).contains("adeack", "adeack2");
  }

  @Test
  void givenEmptyAckListWhenRemoveAckThenNothingHappens() {
    FileReport fileReport = FileReport.createFileReport();

    fileReport.removeAckToDownload("adeack");

    assertThat(fileReport.getAckToDownload()).isNotNull().isEmpty();
  }

  @Test
  void givenAckListWhenRemoveAckThenAckIsRemoved() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addAckToDownload("adeack");
    fileReport.addAckToDownload("adeack2");

    fileReport.removeAckToDownload("adeack");

    assertThat(fileReport.getAckToDownload()).isNotNull().hasSize(1).contains("adeack2");
  }

  @Test
  void givenAckListContainsOnlyOneAckWhenRemoveAckThenCollectionIsEmpty() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addAckToDownload("adeack");

    fileReport.removeAckToDownload("adeack");

    assertThat(fileReport.getAckToDownload()).isNotNull().isEmpty();
  }

  @Test
  void givenTwoFilesInsideDateRangeWhenRemoveOldFilesThenRemoveNothing() {
    FileReport fileReport = FileReport.createFileReport();
    var fileMetadata = FileMetadata.builder().name("file").size(100L)
        .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(5)).build();
    var fileMetadata2 = FileMetadata.builder().name("file2").size(3000L)
        .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(2)).build();
    fileReport.addFileUploaded(fileMetadata);
    fileReport.addFileUploaded(fileMetadata2);
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2);

    fileReport.removeFilesOlderThan(10);

    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2);
  }

  @Test
  void givenOneFileOutDateRangeWhenRemoveOldFilesThenRemoveOneFile() {
    FileReport fileReport = FileReport.createFileReport();
    var fileMetadata = FileMetadata.builder().name("file").size(100L)
        .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(5)).build();
    var fileMetadata2 = FileMetadata.builder().name("file2").size(3000L)
        .status(FileStatusEnum.VALIDATED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(11)).build();
    fileReport.addFileUploaded(fileMetadata);
    fileReport.addFileUploaded(fileMetadata2);
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2);

    fileReport.removeFilesOlderThan(10);

    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1).map(FileMetadata::getName)
        .contains("file");
  }

  @Test
  void givenTwoFileOutDateRangeWhenRemoveOldFilesThenRemoveAllFiles() {
    FileReport fileReport = FileReport.createFileReport();
    var fileMetadata = FileMetadata.builder().name("file").size(100L)
        .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(12)).build();
    var fileMetadata2 = FileMetadata.builder().name("file2").size(3000L)
        .status(FileStatusEnum.VALIDATED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(11)).build();
    fileReport.addFileUploaded(fileMetadata);
    fileReport.addFileUploaded(fileMetadata2);
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2);

    fileReport.removeFilesOlderThan(10);

    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();
  }

  @Test
  void whenRemoveOldFilesWithNullParamThenDoNothing() {
    FileReport fileReport = FileReport.createFileReport();
    var fileMetadata = FileMetadata.builder().name("file").size(100L)
        .status(FileStatusEnum.RECEIVED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(12)).build();
    var fileMetadata2 = FileMetadata.builder().name("file2").size(3000L)
        .status(FileStatusEnum.VALIDATED_BY_PAGOPA)
        .transmissionDate(LocalDateTime.now().minusDays(11)).build();
    fileReport.addFileUploaded(fileMetadata);
    fileReport.addFileUploaded(fileMetadata2);
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2);

    fileReport.removeFilesOlderThan(10);

    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();
  }

  @Test
  void whenCreateAReportWithStatusCodeThenReportIsCorrect() {
    FileReport fileReport = FileReport.createFileReportWithSenderCode("12345");

    assertThat(fileReport).isNotNull();
    assertThat(fileReport.getSenderCodes()).isNotNull().hasSize(1).contains("12345");
    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();
    assertThat(fileReport.getAckToDownload()).isNotNull().isEmpty();
  }
}
