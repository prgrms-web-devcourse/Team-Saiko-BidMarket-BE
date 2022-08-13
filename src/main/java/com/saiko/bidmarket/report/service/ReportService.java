package com.saiko.bidmarket.report.service;

import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.controller.dto.ReportCreateResponse;
import com.saiko.bidmarket.report.entity.Report;

public interface ReportService {

  ReportCreateResponse create(
      long reporterId,
      Report.Type type,
      long typeId,
      ReportCreateRequest createRequest
  );
}
