package com.saiko.bidmarket.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saiko.bidmarket.report.entity.Report;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
  boolean existsByReporter_IdAndTypeAndTypeId(
      long reporterId,
      Report.Type type,
      long typeId
  );

  int countByTypeAndTypeId(
      Report.Type type,
      long typeId
  );
}
