package com.saiko.bidmarket.report.service.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportCreateDto {

  private final String reason;

  private final UnsignedLong fromUserId;

  private final UnsignedLong toUserId;

  @Builder
  private ReportCreateDto(String reason, UnsignedLong fromUserId, UnsignedLong toUserId) {
    Assert.notNull(reason, "reason must be provided");
    Assert.notNull(fromUserId, "fromUserId must be provided");
    Assert.notNull(toUserId, "toUserId must be provided");

    this.reason = reason;
    this.fromUserId = fromUserId;
    this.toUserId = toUserId;
  }
}
