package com.saiko.bidmarket.report.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportCreateResponse {
  private final long id;

  public static ReportCreateResponse from(long id) {
    return new ReportCreateResponse(id);
  }
}
