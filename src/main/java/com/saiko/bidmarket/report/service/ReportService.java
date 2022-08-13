package com.saiko.bidmarket.report.service;

import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.controller.dto.ReportCreateResponse;

public interface ReportService {

  ReportCreateResponse create(
      long fromUserId,
      ReportCreateRequest createRequest
  );
}
