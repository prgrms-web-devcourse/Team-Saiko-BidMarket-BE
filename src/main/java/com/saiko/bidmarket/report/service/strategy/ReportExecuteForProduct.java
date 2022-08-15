package com.saiko.bidmarket.report.service.strategy;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.report.service.ReportValidator;
import com.saiko.bidmarket.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportExecuteForProduct implements ReportExecuteStrategy {

  public static final Report.Type REPORT_TYPE = Report.Type.PRODUCT;

  private final ReportRepository reportRepository;

  private final ProductRepository productRepository;

  private final BiddingRepository biddingRepository;

  private final ReportExecuteForUser reportExecuteForUser;

  private final ReportValidator validator;

  @Override
  @Transactional
  public void execute(
      User reporter,
      long productId,
      String reason
  ) {
    Assert.notNull(reporter, "신고자는 없을 수 없습니다.");
    Assert.hasText(reason, "신고 이유는 비거나 없을 수 없습니다.");

    Product product = productRepository
        .findById(productId)
        .orElseThrow(NotFoundException::new);

    validator.validateDuplicate(reporter.getId(), REPORT_TYPE, product.getId());

    reportRepository.save(Report.of(reporter, REPORT_TYPE, product.getId(), reason));

    checkPenalty(product);

    try {
      reportExecuteForUser.execute(
          reporter,
          product
              .getWriter()
              .getId(),
          reason
      );
    } catch (IllegalArgumentException ignored) {
    }
  }

  private void checkPenalty(Product product) {
    if (validator.isOverMaxReportCount(REPORT_TYPE, product.getId())) {
      biddingRepository.deleteAllBatchByProductId(product.getId());
      product.reportPenalty();
    }
  }
}
