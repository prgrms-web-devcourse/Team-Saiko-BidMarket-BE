package com.saiko.bidmarket.report.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saiko.bidmarket.common.jwt.JwtAuthentication;
import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.controller.dto.ReportCreateResponse;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/reports")
@RequiredArgsConstructor
public class ReportApiController {

  private final ReportService reportService;

  @PostMapping("users/{userId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ReportCreateResponse createToUser(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @PathVariable long userId,
      @RequestBody @Valid
      ReportCreateRequest createRequest
  ) {
    return reportService.create(
        authentication.getUserId(),
        Report.Type.User,
        userId,
        createRequest
    );
  }

  @PostMapping("products/{productId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ReportCreateResponse createToProduct(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @PathVariable long productId,
      @RequestBody @Valid
      ReportCreateRequest createRequest
  ) {
    return reportService.create(
        authentication.getUserId(),
        Report.Type.PRODUCT,
        productId,
        createRequest
    );
  }

  @PostMapping("comments/{commentId}")
  @ResponseStatus(HttpStatus.CREATED)
  public ReportCreateResponse createToComment(
      @AuthenticationPrincipal
      JwtAuthentication authentication,
      @PathVariable long commentId,
      @RequestBody @Valid
      ReportCreateRequest createRequest
  ) {
    return reportService.create(
        authentication.getUserId(),
        Report.Type.COMMENT,
        commentId,
        createRequest
    );
  }

}
