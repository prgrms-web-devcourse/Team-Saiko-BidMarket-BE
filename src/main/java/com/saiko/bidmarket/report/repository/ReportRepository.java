package com.saiko.bidmarket.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
  boolean existsByFromUser_IdAndToUser_Id(
      long fromUserId,
      long toUserId
  );
}
