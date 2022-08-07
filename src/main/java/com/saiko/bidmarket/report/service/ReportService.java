package com.saiko.bidmarket.report.service;

import com.saiko.bidmarket.report.service.dto.ReportCreateDto;

public interface ReportService {
  long create(ReportCreateDto createDto);
}
