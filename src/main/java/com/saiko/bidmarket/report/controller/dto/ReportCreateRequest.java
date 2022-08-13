package com.saiko.bidmarket.report.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import com.saiko.bidmarket.report.entity.Report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportCreateRequest {

  @NotBlank
  private final String reason;

  private final long toUserId;

  @Null
  private final Report.Type type;

  @Null
  private final Long typeId;
}
