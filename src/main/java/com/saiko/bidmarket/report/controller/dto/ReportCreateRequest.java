package com.saiko.bidmarket.report.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Getter;

@Getter
public class ReportCreateRequest {

  @NotBlank
  private final String reason;

  @NotNull
  private final UnsignedLong fromUserId;

  @NotNull
  private final UnsignedLong toUserId;

  @JsonCreator
  public ReportCreateRequest(String reason, long fromUserId, long toUserId) {
    this.reason = reason;
    this.fromUserId = UnsignedLong.valueOf(fromUserId);
    this.toUserId = UnsignedLong.valueOf(toUserId);
  }
}
