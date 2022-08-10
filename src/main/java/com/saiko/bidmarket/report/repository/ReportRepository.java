package com.saiko.bidmarket.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.user.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, UnsignedLong> {
  boolean existsByFromUserAndToUser(
      User fromUser,
      User toUser
  );
}
