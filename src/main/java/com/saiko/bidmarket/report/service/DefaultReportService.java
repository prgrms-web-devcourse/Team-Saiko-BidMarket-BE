package com.saiko.bidmarket.report.service;

import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.report.service.dto.ReportCreateDto;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultReportService implements ReportService {

  private final ReportRepository reportRepository;

  private final UserRepository userRepository;

  @Override
  public UnsignedLong create(ReportCreateDto createDto) {
    Assert.notNull(createDto, "Report create dto must be provided");

    validateProxyReport(createDto.getRequestUserId(), createDto.getFromUserId());

    User fromUser = userRepository
        .findById(createDto
                      .getFromUserId()
                      .getValue())
        .orElseThrow(NotFoundException::new);
    User toUser = userRepository
        .findById(createDto
                      .getToUserId()
                      .getValue())
        .orElseThrow(NotFoundException::new);

    validateSameUserWithFromAndTo(fromUser, toUser);

    Report report = Report
        .builder()
        .reason(createDto.getReason())
        .fromUser(fromUser)
        .toUser(toUser)
        .build();

    return UnsignedLong.valueOf(reportRepository
                                    .save(report)
                                    .getId());
  }

  private void validateSameUserWithFromAndTo(
      User fromUser,
      User toUser
  ) {
    if (reportRepository.existsByFromUserAndToUser(fromUser, toUser)) {
      throw new IllegalArgumentException("신고자(id: " + fromUser.getId() + ")는 "
                                             + "피신고자(id: " + toUser.getId() + ")를 "
                                             + "이미 신고하였습니다.");
    }
  }

  private void validateProxyReport(
      UnsignedLong requestUserId,
      UnsignedLong fromUserId
  ) {
    if (!requestUserId.equals(fromUserId)) {
      throw new AuthorizationServiceException("다른 유저의 신고를 대신할 수 없습니다.");
    }
  }

}
