package com.saiko.bidmarket.report.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.service.strategy.ReportExecuteStrategy;
import com.saiko.bidmarket.report.service.strategy.ReportExecutorFactory;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultReportService implements ReportService {

  private final UserRepository userRepository;

  private final ReportExecutorFactory reportExecutorFactory;

  @Override
  @Transactional
  public void create(
      long reporterId,
      ReportCreateRequest createRequest
  ) {
    Assert.notNull(createRequest, "Report create dto must be provided");

    ReportExecuteStrategy executeStrategy
        = reportExecutorFactory.findExecuteStrategyByType(createRequest.getType());

    User reporter = userRepository
        .findById(reporterId)
        .orElseThrow(NotFoundException::new);

    boolean reportIsCompleted
        = executeStrategy.execute(reporter, createRequest.getTypeId(), createRequest.getReason());

    if (!reportIsCompleted) {
      throw new IllegalArgumentException("신고가 진행되지 않았습니다");
    }
  }
}
