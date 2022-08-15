package com.saiko.bidmarket.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;

@ExtendWith(MockitoExtension.class)
class ReportValidatorTest {

  @Mock
  ReportRepository reportRepository;

  @InjectMocks
  ReportValidator reportValidator;

  private final long reporterId = 1L;

  private final Report.Type type = Report.Type.USER;

  private final long typeId = 1L;

  @Nested
  @DisplayName("isDuplicatedReport 함수는")
  class DescribeIsDuplicatedReport {

    @Nested
    @DisplayName("입력된 인자로된 신고가 존재한다면")
    class ContextExistInputArguments {

      @Test
      @DisplayName("true를 반환한다")
      void ItThrowIllegalArgumentException() {
        // given
        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(anyLong(), any(), anyLong()))
            .willReturn(true);

        // when
        boolean actual = reportValidator.isDuplicatedReport(reporterId, type, typeId);

        // then
        verify(reportRepository, atLeastOnce())
            .existsByReporter_IdAndTypeAndTypeId(anyLong(), any(), anyLong());
        assertThat(actual).isTrue();
      }
    }

    @Nested
    @DisplayName("입력된 인자로된 신고가 존재하지 않는다면")
    class ContextNotExistInputArguments {

      @Test
      @DisplayName("false를 반환한다")
      void ItReturnNothing() {
        // given
        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(anyLong(), any(), anyLong()))
            .willReturn(false);

        // when
        boolean actual = reportValidator.isDuplicatedReport(reporterId, type, typeId);

        // then
        verify(reportRepository, atLeastOnce())
            .existsByReporter_IdAndTypeAndTypeId(anyLong(), any(), anyLong());
        assertThat(actual).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("isOverMaxReportCount 함수는")
  class DescribeIsOverMaxReportCountMethod {

    @Nested
    @DisplayName("신고 횟수가 최대 신고횟수보다 크거나 같다면")
    class ContextReportCountOverThanMaxCount {

      @Test
      @DisplayName("true를 반환한다.")
      void ItReturnTrue() {
        // given
        given(reportRepository.countByTypeAndTypeId(type, typeId)).willReturn(type.MAX_COUNT);

        // when
        boolean actual = reportValidator.isOverMaxReportCount(type, typeId);

        // then
        assertThat(actual).isTrue();
      }
    }

    @Nested
    @DisplayName("신고 횟수가 최대 신고횟수보다 작다면")
    class ContextReportCountlessThanMaxCount {

      @Test
      @DisplayName("false를 반환한다.")
      void ItReturnFalse() {
        // given
        given(reportRepository.countByTypeAndTypeId(type, typeId)).willReturn(type.MAX_COUNT / 2);

        // when
        boolean actual = reportValidator.isOverMaxReportCount(type, typeId);

        // then
        assertThat(actual).isFalse();
      }
    }
  }

}
