package com.saiko.bidmarket.report.service;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.report.service.dto.ReportCreateDto;

public interface ReportService {

  UnsignedLong create(ReportCreateDto createDto);
}
