package com.saiko.bidmarket.report.controller.dto;

import com.saiko.bidmarket.common.entity.UnsignedLong;

import lombok.Getter;

@Getter
public class ReportCreateResponse {

  private final long id;

  public ReportCreateResponse(long id) {
    this.id = id;
  }
}
