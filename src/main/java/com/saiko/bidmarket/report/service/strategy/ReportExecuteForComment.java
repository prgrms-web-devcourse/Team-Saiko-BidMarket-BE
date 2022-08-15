package com.saiko.bidmarket.report.service.strategy;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.comment.repository.CommentRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.report.service.ReportValidator;
import com.saiko.bidmarket.user.entity.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReportExecuteForComment implements ReportExecuteStrategy {

  public static final Report.Type REPORT_TYPE = Report.Type.COMMENT;

  private final ReportRepository reportRepository;

  private final CommentRepository commentRepository;

  private final ReportExecuteForUser reportExecuteForUser;

  private final ReportValidator validator;

  @Override
  @Transactional
  public boolean execute(
      User reporter,
      long commentId,
      String reason
  ) {
    Assert.notNull(reporter, "신고자는 없을 수 없습니다.");
    Assert.hasText(reason, "신고 이유는 비거나 없을 수 없습니다.");

    Comment comment = commentRepository
        .findById(commentId)
        .orElseThrow(NotFoundException::new);

    boolean reportIsDuplicated
        = validator.isDuplicatedReport(reporter.getId(), REPORT_TYPE, comment.getId());

    if (reportIsDuplicated) {
      return false;
    }

    reportRepository.save(Report.of(reporter, REPORT_TYPE, commentId, reason));
    checkPenalty(comment);

    reportExecuteForUser.execute(
        reporter,
        comment
            .getWriter()
            .getId(),
        reason
    );

    return true;
  }

  private void checkPenalty(Comment comment) {
    if (validator.isOverMaxReportCount(REPORT_TYPE, comment.getId())) {
      comment.reportPenalty();
    }
  }
}
