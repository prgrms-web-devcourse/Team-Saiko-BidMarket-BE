package com.saiko.bidmarket.report.service.strategy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.saiko.bidmarket.report.entity.Report;

@ExtendWith(MockitoExtension.class)
public class ReportExecutorFactoryTest {

  @InjectMocks
  private ReportExecutorFactory reportExecutorFactory;

  @Mock
  private ReportExecuteForComment reportExecuteForComment;

  @Mock
  private ReportExecuteForProduct reportExecuteForProduct;

  @Mock
  private ReportExecuteForUser reportExecuteForUser;

  @Nested
  @DisplayName("findExecuteStrategyByType 함수는")
  class DescribeFindExecuteStrategyByTypeMethod {

    @Nested
    @DisplayName("신고 유형이 댓글이라면")
    class ContextCommentReportType {

      @Test
      @DisplayName("댓글 실행 전략 객체를 반환한다")
      void ItReturnReportExecuteForComment() {
        // given
        // when
        ReportExecuteStrategy actual
            = reportExecutorFactory.findExecuteStrategyByType(Report.Type.COMMENT);

        // then
        assertThat(actual).isInstanceOf(ReportExecuteForComment.class);
      }
    }

    @Nested
    @DisplayName("신고 유형이 상품이라면")
    class ContextProductReportType {

      @Test
      @DisplayName("댓글 실행 전략 객체를 반환한다")
      void ItReturnReportExecuteForProduct() {
        // given
        // when
        ReportExecuteStrategy actual
            = reportExecutorFactory.findExecuteStrategyByType(Report.Type.PRODUCT);

        // then
        assertThat(actual).isInstanceOf(ReportExecuteForProduct.class);
      }
    }

    @Nested
    @DisplayName("신고 유형이 사용자이라면")
    class ContextUserReportType {

      @Test
      @DisplayName("댓글 실행 전략 객체를 반환한다")
      void ItReturnReportExecuteForUser() {
        // given
        // when
        ReportExecuteStrategy actual
            = reportExecutorFactory.findExecuteStrategyByType(Report.Type.USER);

        // then
        assertThat(actual).isInstanceOf(ReportExecuteForUser.class);
      }
    }

    @Nested
    @DisplayName("신고 유형이 없거나 정의되지 않았다면")
    class ContextNotExistOrDefineReportType {

      @Test
      @DisplayName("IllegalArgumentException을 발생시킨다")
      void ItThrowIllegalArgumentException() {
        // given
        // when
        // then
        assertThatThrownBy(
            () -> reportExecutorFactory.findExecuteStrategyByType(null)
        ).isInstanceOf(IllegalArgumentException.class);
      }
    }
  }

}
