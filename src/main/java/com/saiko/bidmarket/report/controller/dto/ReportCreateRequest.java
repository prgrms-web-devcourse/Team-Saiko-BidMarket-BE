package com.saiko.bidmarket.report.controller.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public class ReportCreateRequest {

  @NotBlank
  private final String reason;

  @JsonCreator
  public ReportCreateRequest(String reason) {
    this.reason = reason;
  }
}
