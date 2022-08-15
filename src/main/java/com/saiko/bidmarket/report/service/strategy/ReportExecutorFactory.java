package com.saiko.bidmarket.report.service.strategy;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.saiko.bidmarket.report.entity.Report;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportExecutorFactory {

  private final ReportExecuteForComment reportExecuteForComment;
  private final ReportExecuteForProduct reportExecuteForProduct;
  private final ReportExecuteForUser reportExecuteForUser;

  public ReportExecuteStrategy findExecuteStrategyByType(Report.Type type) {
    Assert.notNull(type, "type은 null일 수 없습니다.");

    switch (type) {
      case PRODUCT:
        return reportExecuteForProduct;

      case COMMENT:
        return reportExecuteForComment;

      case USER:
        return reportExecuteForUser;

      default:
        throw new IllegalArgumentException("상품의 유형이 아닙니다.");
    }
  }
}
