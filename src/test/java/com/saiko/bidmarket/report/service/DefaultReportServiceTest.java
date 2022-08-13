package com.saiko.bidmarket.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
import com.saiko.bidmarket.report.controller.dto.ReportCreateResponse;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultReportServiceTest {

  @InjectMocks
  private DefaultReportService reportService;

  @Mock
  private ReportRepository reportRepository;

  @Mock
  private UserRepository userRepository;

  private static final long requestUserId = 1L;

  private static final String reason = "reason";

  private static final User fromUser = User
      .builder()
      .username("fromuUser")
      .profileImage("imageUrl")
      .provider("provider")
      .providerId("providerId")
      .group(new Group())
      .build();

  private static final User toUser = User
      .builder()
      .username("toUser")
      .profileImage("imageUrl")
      .provider("provider2")
      .providerId("providerId2")
      .group(new Group())
      .build();

  @BeforeAll
  static void setUpUsersId() {
    ReflectionTestUtils.setField(fromUser, "id", requestUserId);
    ReflectionTestUtils.setField(toUser, "id", requestUserId * 2);
  }

  @Nested
  @DisplayName("create 메소드는")
  class DescribeCreateMethod {

    @Nested
    @DisplayName("빈 CreateDTo가 들어오면")
    class ContextNullCreateDto {

      @ParameterizedTest
      @NullSource
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException(ReportCreateRequest createRequest) {
        // when
        // then
        assertThatThrownBy(() -> reportService.create(fromUser.getId(), createRequest))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("fromUser를 찾을 수 없는 경우")
    class ContextNotFoundFromUser {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(
            reason,
            toUser.getId(),
            null,
            null
        );

        given(userRepository.findById(fromUser.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(fromUser.getId(), createRequest))
            .isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("toUser를 찾을 수 없는 경우")
    class ContextNotFoundToUser {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(
            reason,
            toUser.getId(),
            null,
            null
        );

        given(userRepository.findById(fromUser.getId())).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(fromUser.getId(), createRequest))
            .isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("fromUser가 이미 toUser를 신고한 경우")
    class ContextExistSameReport {

      @Test
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(
            reason,
            toUser.getId(),
            null,
            null
        );
        given(reportRepository.existsByFromUser_IdAndToUser_Id(fromUser.getId(), toUser.getId()))
            .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> reportService.create(fromUser.getId(), createRequest))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("신고자와 피신고자가 같은 경우")
    class ContextSameUserWithFromAndTo {

      @Test
      @DisplayName("IllegalArgumentException을 던진다.")
      void ItThrowsIllegalArgumentException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(
            reason,
            fromUser.getId(),
            null,
            null
        );

        given(reportRepository.existsByFromUser_IdAndToUser_Id(fromUser.getId(), fromUser.getId()))
            .willReturn(false);
        given(userRepository.findById(fromUser.getId())).willReturn(Optional.of(fromUser));

        // when
        // then
        assertThatThrownBy(() -> reportService.create(fromUser.getId(), createRequest))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("정상적인 Create Dto가 인자로 들어올 경우")
    class ContextValidCreateDto {

      @Test
      @DisplayName("생성된 Report의 Id를 반환한다.")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(
            reason,
            toUser.getId(),
            null,
            null
        );
        Report createdReport = Report.toUser(fromUser, toUser, reason);
        long reportId = 1L;
        ReflectionTestUtils.setField(createdReport, "id", reportId);

        given(reportRepository.existsByFromUser_IdAndToUser_Id(fromUser.getId(), toUser.getId()))
            .willReturn(false);
        given(userRepository.findById(fromUser.getId())).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.of(toUser));
        given(reportRepository.save(any(Report.class))).willReturn(createdReport);

        // when
        ReportCreateResponse actual = reportService.create(fromUser.getId(), createRequest);

        // then
        assertThat(actual.getId()).isEqualTo(reportId);

      }
    }
  }
}
