package com.saiko.bidmarket.report.service.dto;

import org.springframework.util.Assert;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportCreateDto {

  private final UnsignedLong requestUserId;

  private final String reason;

  private final UnsignedLong fromUserId;

  private final UnsignedLong toUserId;


  @Builder
  private ReportCreateDto(UnsignedLong requestUserId, String reason, UnsignedLong fromUserId, UnsignedLong toUserId) {
    Assert.notNull(requestUserId, "Request user id must be provided");
    Assert.hasText(reason, "Reason must contain contexts");
    Assert.notNull(fromUserId, "From user id must be provided");
    Assert.notNull(toUserId, "To userid  must be provided");

    this.requestUserId = requestUserId;
    this.reason = reason;
    this.fromUserId = fromUserId;
    this.toUserId = toUserId;
  }
}


