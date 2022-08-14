package com.saiko.bidmarket.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.service.strategy.ReportExecuteStrategy;
import com.saiko.bidmarket.report.service.strategy.ReportExecutorFactory;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultReportServiceTest {

  @InjectMocks
  private DefaultReportService reportService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReportExecutorFactory reportExecutorFactory;

  @Mock
  private ReportExecuteStrategy reportExecuteStrategy;

  private static final long reporterId = 1L;

  private static final long typeId = 2L;

  private static final String reason = "신고이유";

  private static final User reporter = User
      .builder()
      .username("reporter")
      .profileImage("imageUrl")
      .provider("provider")
      .providerId("providerId")
      .group(new Group())
      .build();

  @BeforeAll
  static void setUpUsersId() {
    ReflectionTestUtils.setField(reporter, "id", reporterId);
  }

  @Nested
  @DisplayName("create 메소드는")
  class DescribeCreateMethod {

    @Nested
    @DisplayName("신고자를 찾을 수 없는 경우")
    class ContextNotFoundReporter {

      @ParameterizedTest
      @EnumSource
      @DisplayName("NotFoundException을 던진다")
      void ItThrowsNotFoundException(Report.Type type) {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(type, typeId, reason);
        given(userRepository.findById(reporterId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporterId,
            createRequest
        )).isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("TypeId로 신고 대상을 찾을 수 없는 경우")
    class ContextNotFoundReportedObject {

      @ParameterizedTest
      @EnumSource
      @DisplayName("NotFoundException을 던진다")
      void ItThrowsNotFoundException(Report.Type type) {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(type, typeId, reason);
        given(userRepository.findById(reporterId)).willReturn(Optional.of(reporter));
        given(reportExecutorFactory.findExecuteStrategyByType(type))
            .willReturn(reportExecuteStrategy);
        doThrow(NotFoundException.class)
            .when(reportExecuteStrategy)
            .execute(any(), anyLong(), anyString());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            createRequest
        )).isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("동일한 신고가 존재할 경우")
    class ContextExistSameReportToUser {

      @ParameterizedTest
      @EnumSource
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException(Report.Type type) {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(type, typeId, reason);
        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(reportExecutorFactory.findExecuteStrategyByType(type))
            .willReturn(reportExecuteStrategy);
        doThrow(IllegalArgumentException.class)
            .when(reportExecuteStrategy)
            .execute(any(), anyLong(), anyString());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporterId,
            createRequest
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }
  }
}
