package com.saiko.bidmarket.report.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.comment.repository.CommentRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.controller.dto.ReportCreateResponse;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultReportService implements ReportService {

  private final ReportRepository reportRepository;

  private final UserRepository userRepository;

  private final ProductRepository productRepository;

  private final CommentRepository commentRepository;

  @Override
  public ReportCreateResponse create(
      long reporterId,
      Report.Type type,
      long typeId,
      ReportCreateRequest createRequest
  ) {
    Assert.notNull(type, "ReportType must be provided");
    Assert.notNull(createRequest, "Report create dto must be provided");

    User reporter = userRepository
        .findById(reporterId)
        .orElseThrow(NotFoundException::new);

    Report report = createReport(reporter, type, typeId, createRequest.getReason());

    return ReportCreateResponse.from(reportRepository
                                         .save(report)
                                         .getId());
  }

  private Report createReport(
      User reporter,
      Report.Type type,
      long typeId,
      String reason
  ) {
    validateDuplicate(reporter.getId(), type, typeId);

    if (type == Report.Type.USER) {
      validateSelfReport(reporter.getId(), typeId);
      User reportedUser = userRepository
          .findById(typeId)
          .orElseThrow(NotFoundException::new);

      return Report.toUser(reporter, reportedUser.getId(), reason);
    }

    if (type == Report.Type.PRODUCT) {
      Product reportedProduct = productRepository
          .findById(typeId)
          .orElseThrow(NotFoundException::new);

      return Report.toProduct(reporter, reportedProduct, reason);
    }

    if (type == Report.Type.COMMENT) {
      Comment reportedComment = commentRepository
          .findById(typeId)
          .orElseThrow(NotFoundException::new);

      return Report.toComment(reporter, reportedComment, reason);
    }

    throw new IllegalArgumentException("정의되지 않은 신고 유형입니다.");
  }

  private static void validateSelfReport(
      long reporterId,
      long userId
  ) {
    if (reporterId == userId) {
      throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
    }
  }

  private void validateDuplicate(
      long reporterId,
      Report.Type type,
      long typeId
  ) {
    if (reportRepository.existsByReporter_IdAndTypeAndTypeId(reporterId, type, typeId)) {
      throw new IllegalArgumentException("이미 신고된 요청입니다.");
    }
  }
}
