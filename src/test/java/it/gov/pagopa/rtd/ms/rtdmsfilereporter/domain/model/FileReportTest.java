package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

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
  void whenAddFileUploadedThenCollectionIsUpdatedCorrectly() {
    FileReport fileReport = FileReport.createFileReport();

    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file1"));
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1)
        .map(FileMetadata::getName).contains("file1");

    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file2"));
    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2)
        .map(FileMetadata::getName).contains("file1", "file2");
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

    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(1)
        .map(FileMetadata::getName).contains("file2");
  }

  @Test
  void givenFilesUploadedWhenRemoveWrongFileUploadedThenNothingHappens() {
    FileReport fileReport = FileReport.createFileReport();
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file1"));
    fileReport.addFileUploaded(FileMetadata.createNewFileMetadata("file2"));

    fileReport.removeFileUploaded("wrongname");

    assertThat(fileReport.getFilesUploaded()).isNotNull().hasSize(2)
        .map(FileMetadata::getName).contains("file1", "file2");
  }

  @Test
  void whenUpdateFileStatusThenCollectionIsUpdatedCorrectly() {
    FileReport fileReport = FileReport.createFileReport();
    var file = FileMetadata.createNewFileMetadataWithStatus("file1", "OK");
    var file2 = FileMetadata.createNewFileMetadataWithStatus("file2", "KO");
    fileReport.addFileUploaded(file);
    fileReport.addFileUploaded(file2);

    assertThat(fileReport.getFilesUploaded()).map(FileMetadata::getName, FileMetadata::getStatus)
        .contains(Tuple.tuple("file1", "OK"), Tuple.tuple("file2", "KO"));

    fileReport.updateFileStatus("file1", "PROCESSING");

    assertThat(fileReport.getFilesUploaded()).map(FileMetadata::getName, FileMetadata::getStatus)
        .contains(Tuple.tuple("file1", "PROCESSING"), Tuple.tuple("file2", "KO"));
  }

  @Test
  void givenEmptyFilesListWhenUpdateFileStatusThenNewFileIsAdded() {
    FileReport fileReport = FileReport.createFileReport();
    assertThat(fileReport.getFilesUploaded()).isNotNull().isEmpty();

    fileReport.updateFileStatus("file1", "PROCESSING");

    assertThat(fileReport.getFilesUploaded()).map(FileMetadata::getName, FileMetadata::getStatus)
        .contains(Tuple.tuple("file1", "PROCESSING"));
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
}