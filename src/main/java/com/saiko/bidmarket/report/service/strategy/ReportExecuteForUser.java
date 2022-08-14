package com.saiko.bidmarket.report.service.strategy;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.report.service.ReportValidator;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportExecuteForUser implements ReportExecuteStrategy {

  private static final Report.Type REPORT_TYPE = Report.Type.USER;

  private final UserRepository userRepository;

  private final ReportRepository reportRepository;

  private final ReportValidator validator;

  @Override
  @Transactional
  public void execute(
      User reporter,
      long userId,
      String reason
  ) {
    Assert.notNull(reporter, "신고자는 없을 수 없습니다.");
    Assert.hasText(reason, "신고 이유는 비거나 없을 수 없습니다.");

    User reportedUser = userRepository
        .findById(userId)
        .orElseThrow(NotFoundException::new);

    validator.validateDuplicate(reporter.getId(), REPORT_TYPE, reportedUser.getId());

    reportRepository.save(Report.of(reporter, REPORT_TYPE, reportedUser.getId(), reason));

    checkPenalty(reportedUser);
  }

  private void checkPenalty(User reportedUser) {
    if (validator.isOverMaxReportCount(REPORT_TYPE, reportedUser.getId())) {
      reportedUser.reportPenalty();
    }
  }
}
