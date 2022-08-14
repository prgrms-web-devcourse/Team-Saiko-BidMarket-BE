package com.saiko.bidmarket.report.service;

import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;

public interface ReportService {

  void create(
      long reporterId,
      ReportCreateRequest createRequest
  );
}
