package it.gov.pagopa.rtd.ms.rtdmsfilereporter.domain.model;

import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class FileReportAggregator implements Collector<FileReport, FileReport, FileReport> {

  @Override
  public Supplier<FileReport> supplier() {
    return FileReport::createFileReport;
  }

  @Override
  public BiConsumer<FileReport, FileReport> accumulator() {
    return FileReport::sumFileReports;
  }

  @Override
  public BinaryOperator<FileReport> combiner() {
    return FileReport::sumFileReports;
  }

  @Override
  public Function<FileReport, FileReport> finisher() {
    return report -> report;
  }

  @Override
  public Set<Characteristics> characteristics() {
    return Collections.emptySet();
  }

  public static FileReportAggregator aggregateFileReports() {
    return new FileReportAggregator();
  }

}
