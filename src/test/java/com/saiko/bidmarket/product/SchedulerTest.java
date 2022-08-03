package com.saiko.bidmarket.product;

import static com.saiko.bidmarket.product.Category.*;
import static org.awaitility.Awaitility.*;
import static org.mockito.BDDMockito.atLeast;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.common.config.ScheduledConfig;
import com.saiko.bidmarket.common.config.ScheduledConfig.Scheduler;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.service.ProductService;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;

@SpringJUnitConfig(ScheduledConfig.class)
public class SchedulerTest {
  @SpyBean
  private Scheduler scheduler;

  @MockBean
  private ProductService productService;

  @Nested
  @DisplayName("1분마다 동작하는 closeProduct 메소드는")
  class DescribeCloseProduct {

    @Nested
    @DisplayName("해당 시간에 종료되어야 할 경매들이 존재한다면")
    class ContextWithProductsThatNeedToClose {

      @Test
      @DisplayName("경매 종료 로직을 실행한다.")
      void ItExecuteClosingProduct() {
        // given
        User writer = new User("제로", "image", "google", "1234", new Group());
        ReflectionTestUtils.setField(writer, "id", 1L);
        Product product = Product.builder()
                                 .title("텀블러 팝니다")
                                 .description("깨끗해요")
                                 .location("강남")
                                 .category(ETC)
                                 .minimumPrice(15000)
                                 .images(Arrays.asList("image1",
                                                       "image2"))
                                 .writer(writer)
                                 .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        BDDMockito.given(productService.findAllThatNeedToClose(any(), any()))
                  .willReturn(List.of(product));

        // when, then
        await()
            .atMost(Duration.ofMinutes(1))
            .untilAsserted(() -> {
              verify(scheduler, atLeast(1)).closeProduct();
              verify(productService).findAllThatNeedToClose(any(), any());
              verify(productService).executeClosingProduct(any());
            });
      }
    }

    @Nested
    @DisplayName("해당 시간에 종료되어야 할 경매들이 존재하지 않는다면")
    class ContextWithNotExistedProducts {

      @Test
      @DisplayName("경매 종료 로직을 실행하지 않는다.")
      void ItDoesNotExecuteClosingProduct() {
        // given
        BDDMockito.given(productService.findAllThatNeedToClose(any(), any())).willReturn(
            Collections.emptyList());

        // when, then
        await()
            .atMost(Duration.ofMinutes(1))
            .untilAsserted(() -> {
              verify(scheduler, atLeast(1)).closeProduct();
              verify(productService).findAllThatNeedToClose(any(), any());
              verify(productService, times(0)).executeClosingProduct(any());
            });
      }
    }
  }
}
