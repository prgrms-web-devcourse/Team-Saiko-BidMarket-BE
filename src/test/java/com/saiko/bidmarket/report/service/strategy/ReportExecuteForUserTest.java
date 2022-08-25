package com.saiko.bidmarket.report.service.strategy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.report.service.ReportValidator;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.entity.UserRole;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ReportExecuteForUserTest {

  @InjectMocks
  private ReportExecuteForUser reportExecuteForUser;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReportRepository reportRepository;

  @Mock
  private ReportValidator validator;

  private static final long reporterId = 1L;

  private static final User reporter = User
      .builder()
      .username("reporter")
      .profileImage("imageUrl")
      .provider("provider")
      .providerId("providerId")
      .userRole(UserRole.ROLE_USER)
      .build();

  private static final long reportedUserId = Long.MAX_VALUE - reporterId;

  private static final User reportedUser = User
      .builder()
      .username("toUser")
      .profileImage("imageUrl")
      .provider("provider2")
      .providerId("providerId2")
      .userRole(UserRole.ROLE_USER)
      .build();

  private static final String reason = "기본 신고 이유";

  @BeforeAll
  static void setUpUsersId() {
    ReflectionTestUtils.setField(reporter, "id", reporterId);
    ReflectionTestUtils.setField(reportedUser, "id", reportedUserId);
  }

  @Nested
  @DisplayName("execute 함수는")
  class DescribeExecuteMethod {

    @Nested
    @DisplayName("신고자가 null이라면")
    class ContextNullReporter {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다")
      void ItThrowIllegalArgumentException() {
        // given
        // when
        // then
        assertThatThrownBy(() -> reportExecuteForUser.execute(null, reportedUserId, reason))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("신고 이유가 없다면")
    class ContextNullOrEmptyReason {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("IllegalArgumentException을 발생시킨다")
      void ItThrowIllegalArgumentException(String localReason) {
        // given
        // when
        // then
        assertThatThrownBy(() -> reportExecuteForUser.execute(
            reporter,
            reportedUserId,
            localReason
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("피 신고자를 찾을 수 없다면")
    class ContextNotFoundReportedUser {

      @Test
      @DisplayName("NotFoundException을 발생시킨다")
      void ItThrowNotFoundException() {
        // given
        given(userRepository.findById(reportedUserId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportExecuteForUser.execute(
            reporter,
            reportedUserId,
            reason
        )).isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("신고자와 피신고자의 식별자가 같다면")
    class ContextSelfReport {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다")
      void ItThrowIllegalArgumentException() {
        // given
        given(userRepository.findById(reporterId)).willReturn(Optional.of(reporter));

        // when
        // then
        assertThatThrownBy(() -> reportExecuteForUser.execute(
            reporter,
            reporterId,
            reason
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("동일한 신고가 있을 경우")
    class ContextExistSameReport {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다")
      void ItThrowIllegalArgumentException() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(reportedUser));
        doThrow(IllegalArgumentException.class)
            .when(validator)
            .isDuplicatedReport(reporterId, Report.Type.USER, reportedUserId);

        // when
        // then
        assertThatThrownBy(() -> reportExecuteForUser.execute(
            reporter,
            reporterId,
            reason
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("정상적인 인자가 들어온다면")
    class ContextValidArguments {

      @Test
      @DisplayName("report 객체를 저장한다")
      void ItSavedReport() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(reportedUser));

        // when
        reportExecuteForUser.execute(reporter, reporterId, reason);

        // then
        verify(reportRepository, atLeastOnce()).save(any());
      }
    }
    
    @Nested
    @DisplayName("피 신고자의 피 신고 횟수가 초과했다면")
    class ContextOverReportCount {

      @Test
      @DisplayName("신고자는 페널티 처리된다")
      void ItSavedReport() {
        // given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(reportedUser));
        given(validator.isOverMaxReportCount(any(), anyLong())).willReturn(true);
        String originReportedUserName = reportedUser.getUsername();

        // when
        reportExecuteForUser.execute(reporter, reporterId, reason);

        // then
        assertThat(reportedUser.getUsername()).isNotEqualTo(originReportedUserName);
      }
    }
  }
}
