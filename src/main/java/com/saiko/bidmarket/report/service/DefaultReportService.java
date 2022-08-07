package com.saiko.bidmarket.report.service;

import org.springframework.stereotype.Service;

import com.saiko.bidmarket.report.service.dto.ReportCreateDto;

@Service
public class DefaultReportService implements ReportService {
  @Override
  public long create(ReportCreateDto createDto) {
    return 0;
  }
}
