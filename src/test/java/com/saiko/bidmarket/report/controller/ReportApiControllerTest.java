package com.saiko.bidmarket.report.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.report.controller.dto.ReportCreateResponse;
import com.saiko.bidmarket.report.service.ReportService;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = ReportApiController.class)
class ReportApiControllerTest extends ControllerSetUp {

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ReportService reportService;

  private static final String BASE_URL = "/api/v1/reports";

  private static final String DEFAULT_REASON = "신고 이유";

  private static final long DEFAULT_REPORTER_ID = 1L;

  private static final long DEFAULT_TYPE_ID = 1L;

  private static final String REPORT_USER_PATH = "/users/{userId}";

  private static final String REPORT_PRODUCT_PATH = "/products/{productId}";

  private static final String REPORT_COMMENTS_PATH = "/comments/{commentId}";

  @Nested
  @DisplayName("createToUser 메서드는")
  @WithMockCustomLoginUser
  class DescribeCreateToUserMethod {

    @Nested
    @DisplayName("신고 이유가 비었거나 공백일 경우")
    class ContextEmptyReason {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("400 Badrequest으로 응답한다.")
      void ItResponseBadRequest(String reason) throws Exception {
        // given
        String requestBody = createRequestBody(reason);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL + REPORT_USER_PATH, DEFAULT_TYPE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("정상적인 요청이 들어올 경우")
    class ContextValidRequest {

      @Test
      @DisplayName("201 Created와 생성된 신고 id를 응답한다.")
      void ItResponseForbidden() throws Exception {
        // given
        String requestBody = createRequestBody(DEFAULT_REASON);

        long createdReportId = 1L;

        given(reportService.create(anyLong(), any(), anyLong(), any()))
            .willReturn(ReportCreateResponse.from(createdReportId));

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL + REPORT_USER_PATH, DEFAULT_TYPE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response
            .andExpect(status().isCreated())
            .andDo(
                document(
                    "Create report to user",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("userId").description("피신고자 식별자")
                    ),
                    requestFields(
                        fieldWithPath("reason")
                            .type(JsonFieldType.STRING)
                            .description("신고 이유")
                    ),
                    responseFields(
                        fieldWithPath("id")
                            .type(JsonFieldType.NUMBER)
                            .description("신고 식별자"))
                )
            );

      }
    }
  }

  @Nested
  @DisplayName("createToProduct 메서드는")
  @WithMockCustomLoginUser
  class DescribeCreateToProductMethod {

    @Nested
    @DisplayName("신고 이유가 비었거나 공백일 경우")
    class ContextEmptyReason {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("400 Badrequest으로 응답한다.")
      void ItResponseBadRequest(String reason) throws Exception {
        // given
        String requestBody = createRequestBody(reason);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL + REPORT_PRODUCT_PATH, DEFAULT_TYPE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("정상적인 요청이 들어올 경우")
    class ContextValidRequest {

      @Test
      @DisplayName("201 Created와 생성된 신고 id를 응답한다.")
      void ItResponseForbidden() throws Exception {
        // given
        String requestBody = createRequestBody(DEFAULT_REASON);

        long createdReportId = 1L;

        given(reportService.create(anyLong(), any(), anyLong(), any()))
            .willReturn(ReportCreateResponse.from(createdReportId));

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL + REPORT_PRODUCT_PATH, DEFAULT_TYPE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response
            .andExpect(status().isCreated())
            .andDo(
                document(
                    "Create report to product",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("productId").description("상품 식별자")
                    ),
                    requestFields(
                        fieldWithPath("reason")
                            .type(JsonFieldType.STRING)
                            .description("신고 이유")
                    ),
                    responseFields(
                        fieldWithPath("id")
                            .type(JsonFieldType.NUMBER)
                            .description("신고 식별자"))
                )
            );

      }
    }
  }

  @Nested
  @DisplayName("createToComment 메서드는")
  @WithMockCustomLoginUser
  class DescribeCreateToCommentMethod {

    @Nested
    @DisplayName("신고 이유가 비었거나 공백일 경우")
    class ContextEmptyReason {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("400 Badrequest으로 응답한다.")
      void ItResponseBadRequest(String reason) throws Exception {
        // given
        String requestBody = createRequestBody(reason);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL + REPORT_COMMENTS_PATH, DEFAULT_TYPE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("정상적인 요청이 들어올 경우")
    class ContextValidRequest {

      @Test
      @DisplayName("201 Created와 생성된 신고 id를 응답한다.")
      void ItResponseForbidden() throws Exception {
        // given
        String requestBody = createRequestBody(DEFAULT_REASON);

        long createdReportId = 1L;

        given(reportService.create(anyLong(), any(), anyLong(), any()))
            .willReturn(ReportCreateResponse.from(createdReportId));

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL + REPORT_COMMENTS_PATH, DEFAULT_TYPE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response
            .andExpect(status().isCreated())
            .andDo(
                document(
                    "Create report to comment",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("commentId").description("댓글 식별자")
                    ),
                    requestFields(
                        fieldWithPath("reason")
                            .type(JsonFieldType.STRING)
                            .description("신고 이유")
                    ),
                    responseFields(
                        fieldWithPath("id")
                            .type(JsonFieldType.NUMBER)
                            .description("신고 식별자"))
                )
            );

      }
    }
  }

  private String createRequestBody(String reason) throws JsonProcessingException {
    HashMap<String, Object> requestMap = new HashMap<>();
    requestMap.put("reason", reason);

    return objectMapper.writeValueAsString(requestMap);
  }
}
