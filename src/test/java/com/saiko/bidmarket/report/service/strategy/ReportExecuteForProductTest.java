package com.saiko.bidmarket.report.service.strategy;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
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

import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.report.entity.Report;
import com.saiko.bidmarket.report.repository.ReportRepository;
import com.saiko.bidmarket.report.service.ReportValidator;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

@ExtendWith(MockitoExtension.class)
public class ReportExecuteForProductTest {

  @InjectMocks
  private ReportExecuteForProduct reportExecuteForProduct;

  @Mock
  private ReportRepository reportRepository;

  @Mock
  private BiddingRepository biddingRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private ReportExecuteForUser reportExecuteForUser;

  @Mock
  private ReportValidator validator;

  private static final long reporterId = 1L;

  private static final User reporter = User
      .builder()
      .username("reporter")
      .profileImage("imageUrl")
      .provider("provider")
      .providerId("providerId")
      .group(new Group())
      .build();

  private static final long reportedUserId = Long.MAX_VALUE - reporterId;

  private static final User reportedUser = User
      .builder()
      .username("toUser")
      .profileImage("imageUrl")
      .provider("provider2")
      .providerId("providerId2")
      .group(new Group())
      .build();

  private static final long reportedProductId = 1L;

  private static final Product reportedProduct = Product
      .builder()
      .title("title")
      .minimumPrice(1000)
      .description("상품 설명")
      .category(Category.ETC)
      .images(List.of("imageUrl"))
      .location("location")
      .writer(reportedUser)
      .build();

  private static final String reason = "기본 신고 이유";

  @BeforeAll
  static void setUpUsersId() {
    ReflectionTestUtils.setField(reporter, "id", reporterId);
    ReflectionTestUtils.setField(reportedUser, "id", reportedUserId);
    ReflectionTestUtils.setField(reportedProduct, "id", reportedProductId);
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
        assertThatThrownBy(() -> reportExecuteForProduct.execute(
            null,
            reportedProductId,
            reason
        )).isInstanceOf(IllegalArgumentException.class);
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
        assertThatThrownBy(() -> reportExecuteForProduct.execute(
            reporter,
            reportedProductId,
            localReason
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("신고하려는 상품을 찾을 수 없다면")
    class ContextNotFoundReportedProduct {

      @Test
      @DisplayName("NotFoundException을 발생시킨다")
      void ItThrowNotFoundException() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> reportExecuteForProduct.execute(
            reporter,
            reportedProductId,
            reason
        )).isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("동일한 신고가 있을 경우")
    class ContextExistSameReport {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다")
      void ItThrowIllegalArgumentException() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.of(reportedProduct));
        doThrow(IllegalArgumentException.class)
            .when(validator)
            .validateDuplicate(reporterId, Report.Type.PRODUCT, reportedProductId);

        // when
        // then
        assertThatThrownBy(() -> reportExecuteForProduct.execute(
            reporter,
            reportedProductId,
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
        given(productRepository.findById(anyLong())).willReturn(Optional.of(reportedProduct));

        // when
        reportExecuteForProduct.execute(reporter, reportedProductId, reason);

        // then
        verify(reportRepository, atLeastOnce()).save(any());
      }

      @Test
      @DisplayName("상품의 작성자도 신고 요청한다")
      void ItReportProductWriter() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.of(reportedProduct));

        // when
        reportExecuteForProduct.execute(reporter, reportedProductId, reason);

        // then
        verify(reportExecuteForUser, atLeastOnce()).execute(any(), anyLong(), anyString());
      }

    }

    @Nested
    @DisplayName("신고 상품의 피 신고 횟수가 초과했다면")
    class ContextOverReportCount {

      @Test
      @DisplayName("신고 상품은 페널티 처리된다")
      void ItSavedReport() {
        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.of(reportedProduct));
        given(validator.isOverMaxReportCount(any(), anyLong())).willReturn(true);
        String originReportedProductTitle = reportedProduct.getTitle();

        // when
        reportExecuteForProduct.execute(reporter, reportedProductId, reason);

        // then
        assertThat(reportedProduct.getTitle()).isNotEqualTo(originReportedProductTitle);
      }
    }
  }
}
