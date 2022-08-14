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
import com.saiko.bidmarket.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/reports")
@RequiredArgsConstructor
public class ReportApiController {

  private final ReportService reportService;

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public void create(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @RequestBody @Valid
      ReportCreateRequest createRequest
  ) {
    reportService.create(
        authentication.getUserId(),
        createRequest
    );
  }
}
