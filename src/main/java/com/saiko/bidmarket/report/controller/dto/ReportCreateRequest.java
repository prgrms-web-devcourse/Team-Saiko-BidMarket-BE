package com.saiko.bidmarket.report.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.saiko.bidmarket.report.entity.Report;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReportCreateRequest {

  @NotNull
  private final Report.Type type;

  private final long typeId;

  @NotBlank
  private final String reason;
}
