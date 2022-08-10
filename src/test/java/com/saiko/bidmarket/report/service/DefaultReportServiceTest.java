package com.saiko.bidmarket.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.report.service.dto.ReportCreateDto;
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

      @Test
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException() {
        // given
        ReportCreateDto createDto = null;

        // when
        // then
        assertThatThrownBy(() -> reportService.create(createDto))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("requestUserId와 fromUserId가 다른 경우")
    class ContextNotSameIdWithRequestUserAndFromUser {

      @Test
      @DisplayName("AuthorizationServiceException을 던진다")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateDto createDto = ReportCreateDto
            .builder()
            .requestUserId(UnsignedLong.valueOf(Long.MAX_VALUE - fromUser.getId()))
            .reason(reason)
            .fromUserId(UnsignedLong.valueOf(fromUser.getId()))
            .toUserId(UnsignedLong.valueOf(toUser.getId()))
            .build();

        // when
        // then
        assertThatThrownBy(() -> reportService.create(createDto))
            .isInstanceOf(AuthorizationServiceException.class);

      }
    }

    @Nested
    @DisplayName("fromUser를 찾을 수 없는 경우")
    class ContextNotFoundFromUser {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateDto createDto = ReportCreateDto
            .builder()
            .requestUserId(UnsignedLong.valueOf(fromUser.getId()))
            .reason(reason)
            .fromUserId(UnsignedLong.valueOf(fromUser.getId()))
            .toUserId(UnsignedLong.valueOf(toUser.getId()))
            .build();

        given(userRepository.findById(fromUser.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(createDto))
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
        ReportCreateDto createDto = ReportCreateDto
            .builder()
            .requestUserId(UnsignedLong.valueOf(fromUser.getId()))
            .reason(reason)
            .fromUserId(UnsignedLong.valueOf(fromUser.getId()))
            .toUserId(UnsignedLong.valueOf(toUser.getId()))
            .build();

        given(userRepository.findById(fromUser.getId())).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(createDto))
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
        ReportCreateDto createDto = ReportCreateDto
            .builder()
            .requestUserId(UnsignedLong.valueOf(fromUser.getId()))
            .reason(reason)
            .fromUserId(UnsignedLong.valueOf(fromUser.getId()))
            .toUserId(UnsignedLong.valueOf(toUser.getId()))
            .build();

        given(userRepository.findById(fromUser.getId())).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.of(toUser));
        given(reportRepository.existsByFromUserAndToUser(fromUser, toUser))
            .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> reportService.create(createDto))
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
        User toUser = User
            .builder()
            .username(fromUser.getUsername())
            .profileImage(fromUser.getProfileImage())
            .provider("provider")
            .providerId("providerId")
            .group(fromUser.getGroup())
            .build();

        ReflectionTestUtils.setField(toUser, "id", fromUser.getId());

        ReportCreateDto createDto = ReportCreateDto
            .builder()
            .requestUserId(UnsignedLong.valueOf(fromUser.getId()))
            .reason(reason)
            .fromUserId(UnsignedLong.valueOf(fromUser.getId()))
            .toUserId(UnsignedLong.valueOf(toUser.getId()))
            .build();

        given(userRepository.findById(fromUser.getId())).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.of(toUser));
        given(reportRepository.existsByFromUserAndToUser(fromUser, toUser))
            .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> reportService.create(createDto))
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
        ReportCreateDto createDto = ReportCreateDto
            .builder()
            .requestUserId(UnsignedLong.valueOf(fromUser.getId()))
            .reason(reason)
            .fromUserId(UnsignedLong.valueOf(fromUser.getId()))
            .toUserId(UnsignedLong.valueOf(toUser.getId()))
            .build();
        UnsignedLong expected = UnsignedLong.valueOf(1L);

        Report createdReport = Report
            .builder()
            .reason(reason)
            .fromUser(fromUser)
            .toUser(toUser)
            .build();
        ReflectionTestUtils.setField(createdReport, "id", expected.getValue());

        given(userRepository.findById(fromUser.getId())).willReturn(Optional.of(fromUser));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.of(toUser));
        given(reportRepository.existsByFromUserAndToUser(fromUser, toUser))
            .willReturn(false);
        given(reportRepository.save(any(Report.class))).willReturn(createdReport);

        // when
        UnsignedLong actual = reportService.create(createDto);

        // then
        assertThat(actual).isEqualTo(expected);

      }
    }
  }
}
