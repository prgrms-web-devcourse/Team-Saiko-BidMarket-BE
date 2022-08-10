package com.saiko.bidmarket.report.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.common.entity.UnsignedLong;
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
    ReportCreateDto createDto = ReportCreateDto
        .builder()
        .requestUserId(
            UnsignedLong.valueOf(authentication.getUserId()))
        .reason(createRequest.getReason())
        .fromUserId(createRequest.getFromUserId())
        .toUserId(createRequest.getToUserId())
        .build();

    return new ReportCreateResponse(reportService.create(createDto));
  }
}
