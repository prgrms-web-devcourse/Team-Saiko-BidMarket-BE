package com.saiko.bidmarket.report.service;

import org.springframework.stereotype.Component;

import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportValidator {

  private final ReportRepository reportRepository;

  public boolean isDuplicatedReport(
      long reporterId,
      Report.Type type,
      long typeId
  ) {
    return reportRepository.existsByReporter_IdAndTypeAndTypeId(reporterId, type, typeId);
  }

  public boolean isOverMaxReportCount(
      Report.Type type,
      long typeId
  ) {
    int reportCount = reportRepository.countByTypeAndTypeId(type, typeId);

    return type.MAX_COUNT <= reportCount;
  }
}
