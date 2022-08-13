package com.saiko.bidmarket.report.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
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

import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.comment.repository.CommentRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
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

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CommentRepository commentRepository;

  private static final long requestUserId = 1L;

  private static final String reason = "reason";

  private static final User reporter = User
      .builder()
      .username("reporter")
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

  private static final Product toProduct = Product
      .builder()
      .title("title")
      .minimumPrice(1000)
      .description("상품 설명")
      .category(Category.ETC)
      .images(List.of("imageUrl"))
      .location("location")
      .writer(toUser)
      .build();

  private static final Comment toComment = Comment
      .builder()
      .content("comment")
      .product(toProduct)
      .writer(toUser)
      .build();

  @BeforeAll
  static void setUpUsersId() {
    ReflectionTestUtils.setField(reporter, "id", requestUserId);
    ReflectionTestUtils.setField(toUser, "id", requestUserId * 2);
    ReflectionTestUtils.setField(toProduct, "id", requestUserId * 2);
    ReflectionTestUtils.setField(toComment, "id", requestUserId * 2);
  }

  @Nested
  @DisplayName("create 메소드는")
  class DescribeCreateMethod {

    @Nested
    @DisplayName("repoter를 찾을 수 없는 경우")
    class ContextNotFoundReporter {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItThrowsNotFoundException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.USER,
            toUser.getId(),
            createRequest
        )).isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("빈 Type이 들어오면")
    class ContextNullType {

      @ParameterizedTest
      @NullSource
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException(Report.Type type) {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        // when
        // then
        assertThatThrownBy(() -> reportService.create(reporter.getId(), type, 1L, createRequest))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("TypeId로 신고 대상을 찾을 수 없는 경우(유저 신고)")
    class ContextNotFoundToUser {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItThrowsNotFoundException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.USER,
            toUser.getId(),
            createRequest
        )).isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("TypeId로 신고 대상을 찾을 수 없는 경우(상품 신고)")
    class ContextNotFoundToProduct {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItThrowsNotFoundException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(productRepository.findById(toProduct.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.PRODUCT,
            toProduct.getId(),
            createRequest
        )).isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("TypeId로 신고 대상을 찾을 수 없는 경우(댓글 신고)")
    class ContextNotFoundToComment {

      @Test
      @DisplayName("NotFoundException을 던진다")
      void ItThrowsNotFoundException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(commentRepository.findById(toComment.getId())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.COMMENT,
            toComment.getId(),
            createRequest
        )).isInstanceOf(NotFoundException.class);

      }
    }

    @Nested
    @DisplayName("신고자가 이미 해당 사용자를 신고한 경우")
    class ContextExistSameReportToUser {

      @Test
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(
            reporter.getId(),
            Report.Type.USER,
            toUser.getId()
        )).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.USER,
            toUser.getId(),
            createRequest
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("신고자가 이미 해당 상품을 신고한 경우")
    class ContextExistSameReportToProduct {

      @Test
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(
            reporter.getId(),
            Report.Type.PRODUCT,
            toProduct.getId()
        )).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.PRODUCT,
            toProduct.getId(),
            createRequest
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("신고자가 이미 해당 댓글을 신고한 경우")
    class ContextExistSameReportToComment {

      @Test
      @DisplayName("IllegalArgumentException을 던진다")
      void ItThrowsIllegalArgumentException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(
            reporter.getId(),
            Report.Type.COMMENT,
            toComment.getId()
        )).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.COMMENT,
            toComment.getId(),
            createRequest
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("신고자와 피신고자가 같은 경우")
    class ContextSameUserWithFromAndTo {

      @Test
      @DisplayName("IllegalArgumentException을 던진다.")
      void ItThrowsIllegalArgumentException() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));

        // when
        // then
        assertThatThrownBy(() -> reportService.create(
            reporter.getId(),
            Report.Type.USER,
            reporter.getId(),
            createRequest
        )).isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("정상적인 유저 신고 인자로 들어올 경우")
    class ContextValidArgumentsToUser {

      @Test
      @DisplayName("생성된 Report의 Id를 반환한다.")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        Report createdReport = Report.toUser(reporter, toUser.getId(), reason);
        long reportId = 1L;
        ReflectionTestUtils.setField(createdReport, "id", reportId);

        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(
            reporter.getId(),
            Report.Type.USER,
            toUser.getId()
        )).willReturn(false);
        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(userRepository.findById(toUser.getId())).willReturn(Optional.of(toUser));
        given(reportRepository.save(any(Report.class))).willReturn(createdReport);

        // when
        ReportCreateResponse actual = reportService.create(
            reporter.getId(),
            Report.Type.USER,
            toUser.getId(),
            createRequest
        );

        // then
        assertThat(actual.getId()).isEqualTo(reportId);
      }
    }

    @Nested
    @DisplayName("정상적인 상품 신고 인자로 들어올 경우")
    class ContextValidArgumentsToProduct {

      @Test
      @DisplayName("생성된 Report의 Id를 반환한다.")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        Report createdReport = Report.toProduct(reporter, toProduct, reason);
        long reportId = 1L;
        ReflectionTestUtils.setField(createdReport, "id", reportId);

        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(
            reporter.getId(),
            Report.Type.PRODUCT,
            toProduct.getId()
        )).willReturn(false);
        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(productRepository.findById(toProduct.getId())).willReturn(Optional.of(toProduct));
        given(reportRepository.save(any(Report.class))).willReturn(createdReport);

        // when
        ReportCreateResponse actual = reportService.create(
            reporter.getId(),
            Report.Type.PRODUCT,
            toProduct.getId(),
            createRequest
        );

        // then
        assertThat(actual.getId()).isEqualTo(reportId);
      }
    }

    @Nested
    @DisplayName("정상적인 댓글 신고 인자로 들어올 경우")
    class ContextValidArgumentsToComment {

      @Test
      @DisplayName("생성된 Report의 Id를 반환한다.")
      void ItReturnCreatedReportId() {
        // given
        ReportCreateRequest createRequest = new ReportCreateRequest(reason);

        Report createdReport = Report.toComment(reporter, toComment, reason);
        long reportId = 1L;
        ReflectionTestUtils.setField(createdReport, "id", reportId);

        given(reportRepository.existsByReporter_IdAndTypeAndTypeId(
            reporter.getId(),
            Report.Type.COMMENT,
            toComment.getId()
        )).willReturn(false);
        given(userRepository.findById(reporter.getId())).willReturn(Optional.of(reporter));
        given(commentRepository.findById(toComment.getId())).willReturn(Optional.of(toComment));
        given(reportRepository.save(any(Report.class))).willReturn(createdReport);

        // when
        ReportCreateResponse actual = reportService.create(
            reporter.getId(),
            Report.Type.COMMENT,
            toComment.getId(),
            createRequest
        );

        // then
        assertThat(actual.getId()).isEqualTo(reportId);
      }
    }
  }
}
