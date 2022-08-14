package com.saiko.bidmarket.report.service;

import org.springframework.stereotype.Component;

import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportValidator {

  private final ReportRepository reportRepository;

  public void validateDuplicate(
      long reporterId,
      Report.Type type,
      long typeId
  ) {
    if (reportRepository.existsByReporter_IdAndTypeAndTypeId(reporterId, type, typeId)) {
      throw new IllegalArgumentException("중복된 신고입니다.");
    }
  }

  public void validateSelfReport(
      long reporterId,
      long userId
  ) {
    if (reporterId == userId) {
      throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
    }
  }

  public boolean isOverMaxReportCount(
      Report.Type type,
      long typeId
  ) {
    int reportCount = reportRepository.countByTypeAndTypeId(type, typeId);

    return (type.MAX_COUNT <= reportCount);
  }
}
