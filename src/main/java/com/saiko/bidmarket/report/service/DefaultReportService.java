package com.saiko.bidmarket.report.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.exception.NotFoundException;
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

  @Override
  public ReportCreateResponse create(
      long fromUserId,
      ReportCreateRequest createRequest
  ) {
    Assert.notNull(createRequest, "Report create dto must be provided");

    validateSameUserWithFromAndTo(fromUserId, createRequest.getToUserId());

    User fromUser = userRepository
        .findById(fromUserId)
        .orElseThrow(NotFoundException::new);

    User toUser = userRepository
        .findById(createRequest.getToUserId())
        .orElseThrow(NotFoundException::new);

    Report report = Report.toUser(fromUser, toUser, createRequest.getReason());

    return ReportCreateResponse.from(reportRepository
                                         .save(report)
                                         .getId());
  }

  private void validateSameUserWithFromAndTo(
      long fromUserId,
      long toUserId
  ) {
    if (reportRepository.existsByFromUser_IdAndToUser_Id(fromUserId, toUserId)) {
      throw new IllegalArgumentException("이미 신고처리된 요청입니다.");
    }
  }

}
