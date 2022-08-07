package com.saiko.bidmarket.report.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.common.jwt.JwtAuthentication;
import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.controller.dto.ReportCreateResponse;
import com.saiko.bidmarket.report.service.ReportService;
import com.saiko.bidmarket.report.service.dto.ReportCreateDto;

@RestController
@RequestMapping("api/v1/reports")
public class ReportApiController {

  private final ReportService reportService;

  public ReportApiController(ReportService reportService) {
    this.reportService = reportService;
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public ReportCreateResponse create(
      @AuthenticationPrincipal JwtAuthentication authentication,
      @RequestBody @Valid ReportCreateRequest createRequest
  ) {
    if (authentication.getUserId() != createRequest.getFromUserId().getValue()) {
      throw new IllegalArgumentException("다른 유저의 신고를 대신할 수 없습니다.");
    }

    if (createRequest.getToUserId() == createRequest.getFromUserId()) {
      throw new IllegalArgumentException("자기 자신을 신고할 수 없습니다.");
    }

    ReportCreateDto createDto = ReportCreateDto.builder()
                                               .reason(createRequest.getReason())
                                               .fromUserId(createRequest.getFromUserId())
                                               .toUserId(createRequest.getToUserId())
                                               .build();

    return new ReportCreateResponse(reportService.create(createDto));
  }
}
