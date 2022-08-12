package com.saiko.bidmarket.bidding.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.bidding.controller.dto.BiddingCreateRequest;
import com.saiko.bidmarket.bidding.service.BiddingService;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = BiddingApiController.class)
class BiddingApiControllerTest extends ControllerSetUp {

  private static final int MIN_AMOUNT = 1_000;

  private static final int UNIT_AMOUNT = 100;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private BiddingService biddingService;

  public static final String BASE_URL = "/api/v1/biddings";

  static class BiddingPriceSourceOutOfRange implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(Long.MIN_VALUE),
          Arguments.of(-1),
          Arguments.of(0),
          Arguments.of(MIN_AMOUNT - 1),
          Arguments.of(MIN_AMOUNT + (UNIT_AMOUNT / 2))
      );
    }
  }

  @Nested
  @DisplayName("create 함수는")
  @WithMockCustomLoginUser
  class DescribeCreateMethod {

    @Nested
    @DisplayName("잘못된 비딩 금액이 들어오면")
    class ContextInvalidBiddingPrice {

      @ParameterizedTest
      @ArgumentsSource(BiddingPriceSourceOutOfRange.class)
      @DisplayName("BadRequest로 응답한다.")
      void ItResponseWithBadRequest(long invalidBiddingPrice) throws Exception {
        // given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("productId", 1);
        requestMap.put("biddingPrice", invalidBiddingPrice);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        given(biddingService.create(anyLong(), any())).willThrow(IllegalArgumentException.class);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // then
        response.andExpect(status().isBadRequest());

      }
    }

    @Nested
    @DisplayName("비딩하려는 상품이 없는 경우")
    class ContextNotExistBiddingProduct {

      @Test
      @DisplayName("NotFound으로 응답한다.")
      void ItResponseWithNotFound() throws Exception {
        // given
        long notExistProductId = 1L;

        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("productId", notExistProductId);
        requestMap.put("biddingPrice", MIN_AMOUNT);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        given(biddingService.create(anyLong(), any(BiddingCreateRequest.class)))
            .willThrow(NotFoundException.class);

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .post(BASE_URL)
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(requestBody));

        // then
        response.andExpect(status().isNotFound());

      }
    }

    @Nested
    @DisplayName("정상적인 요청된 경우")
    class ContextValidRequest {

      @Test
      @DisplayName("Created와 생성된 bidding id를 응답한다.")
      void ItResponseWithBiddingId() throws Exception {
        // given
        long productId = 1L;

        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("productId", productId);
        requestMap.put("biddingPrice", MIN_AMOUNT);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        long biddingId = 1L;
        given(biddingService.create(anyLong(), any(BiddingCreateRequest.class)))
            .willReturn(biddingId);

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .post(BASE_URL)
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(requestBody));

        // then
        response
            .andExpect(status().isCreated())
            .andDo(document(
                "create bidding",
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("productId")
                        .type(JsonFieldType.NUMBER)
                        .description("비딩할 상품 식별자"),
                    fieldWithPath("biddingPrice")
                        .type(JsonFieldType.NUMBER)
                        .description("비딩하려는 금액")
                ),
                responseFields(
                    fieldWithPath("id")
                        .type(JsonFieldType.NUMBER)
                        .description("생성된 비딩의 식별자")
                )
            ));

      }
    }
  }

  @Nested
  @DisplayName("findBiddingPriceByUserIdAndProductId 함수는")
  @WithMockCustomLoginUser
  class DescribeFindBiddingPriceByUserIdAndProductId {
    private final static String API_PATH = "/products/{productId}";

    @Nested
    @DisplayName("요청자가 비더가 아니라면")
    class ContextWithNotBidder {

      @Test
      @DisplayName("NotFound으로 응답한다.")
      void ItResponseWithNotFound() throws Exception {
        // given
        long productId = 1L;
        given(biddingService.findBiddingPriceByProductIdAndUserId(anyLong(), anyLong()))
            .willThrow(NotFoundException.class);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.get(BASE_URL + API_PATH, productId));

        // then
        response.andExpect(status().isNotFound());

      }
    }

    @Nested
    @DisplayName("정상적인 요청된 경우")
    class ContextValidRequest {

      @Test
      @DisplayName("OK와 Bidding price를 응답한다.")
      void ItResponseWithBiddingPrice() throws Exception {
        // given
        long productId = 1L;

        given(biddingService.findBiddingPriceByProductIdAndUserId(anyLong(), anyLong()))
            .willReturn(10000L);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.get(BASE_URL + API_PATH, productId));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "find bidding price by product id and bidder Id",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("biddingPrice")
                        .type(JsonFieldType.NUMBER)
                        .description("요청자가 해당 상품에비딩한 금액")
                )
            ));

      }
    }
  }
}
