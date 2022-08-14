package com.saiko.bidmarket.report.service.strategy;

import org.springframework.stereotype.Component;

import com.saiko.bidmarket.report.entity.Report;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportExecutorFactory {

  private final ReportExecuteForComment reportExecuteForComment;
  private final ReportExecuteForProduct reportExecuteForProduct;
  private final ReportExecuteForUser reportExecuteForUser;

  public ReportExecuteStrategy findExecuteStrategyByType(Report.Type type) {

    if (type == Report.Type.COMMENT) {
      return reportExecuteForComment;
    }

    if (type == Report.Type.PRODUCT) {
      return reportExecuteForProduct;
    }

    if (type == Report.Type.USER) {
      return reportExecuteForUser;
    }

    throw new IllegalArgumentException("신고 타입이 아닙니다.");
  }
}
