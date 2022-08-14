package com.saiko.bidmarket.report.service.strategy;

import com.saiko.bidmarket.user.entity.User;

public interface ReportExecuteStrategy {

  void execute(
      User reporter,
      long targetId,
      String reason
  );
}
