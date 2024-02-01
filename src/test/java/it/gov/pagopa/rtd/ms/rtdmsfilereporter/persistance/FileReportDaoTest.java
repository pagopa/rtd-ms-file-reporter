package it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance;

import static org.assertj.core.api.Assertions.assertThat;

import it.gov.pagopa.rtd.ms.rtdmsfilereporter.TestUtils;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntity;
import it.gov.pagopa.rtd.ms.rtdmsfilereporter.persistance.model.FileReportEntityMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataMongoTest
class FileReportDaoTest {

  @Container
  public static final MongoDBContainer mongoContainer = new MongoDBContainer("mongo:4.4.4");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
  }

  @Autowired
  FileReportDao dao;

  @Autowired
  MongoTemplate mongoTemplate;

  FileReportEntityMapper mapper = Mappers.getMapper(FileReportEntityMapper.class);

  @BeforeEach
  void setUp() {

  }

  @AfterEach
  void tearDown() {
    mongoTemplate.dropCollection(FileReportEntity.class);
  }

  @Test
  void givenNoResultsWhenFindBySenderCodeThenReturnsEmptyList() {
    var entities = dao.findBySenderCodeIn(Collections.singleton("12345"));

    assertThat(entities).isNotNull().isEmpty();
  }

  @Test
  void givenOneReportWhenFindBySenderCodeThenReturnsOneReport() {
    var fileReport = TestUtils.createFileReport(2, 3);
    fileReport.setSenderCodes(List.of("12345"));
    dao.save(mapper.domainToEntity(fileReport));

    var entities = dao.findBySenderCodeIn(List.of("12345"));

    assertThat(entities).isNotNull().hasSize(1)
        .map(FileReportEntity::getSenderCode).contains("12345");
  }

  @Test
  void givenThreeReportsWhenFindBySenderCodeThenReturnsSelectedReports() {
    var fileReport = TestUtils.createFileReport(2, 3);
    fileReport.setSenderCodes(List.of("12345"));
    var fileReport2 = TestUtils.createFileReport(1, 2);
    fileReport2.setSenderCodes(List.of("67890"));
    var fileReport3 = TestUtils.createFileReport(1, 2);
    fileReport3.setSenderCodes(List.of("46801"));
    dao.save(mapper.domainToEntity(fileReport));
    dao.save(mapper.domainToEntity(fileReport2));
    dao.save(mapper.domainToEntity(fileReport3));

    var entities = dao.findBySenderCodeIn(List.of("12345", "67890"));

    assertThat(entities).isNotNull().hasSize(2)
        .map(FileReportEntity::getSenderCode).contains("12345", "67890");
  }
}