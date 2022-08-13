package com.saiko.bidmarket.report.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.report.controller.dto.ReportCreateRequest;
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

  public static final String BASE_URL = "/api/v1/reports";

  public static final String BASE_REASON = "신고 이유";

  public static final long DEFAULT_AUTH_USER_ID = 1L;

  public static final long BASE_FROM_USER_ID = DEFAULT_AUTH_USER_ID;

  public static final long BASE_TO_USER_ID = 2L;

  @Nested
  @DisplayName("create 메서드는")
  @WithMockCustomLoginUser
  class DescribeCreateMethod {

    @Nested
    @DisplayName("신고 이유가 비었거나 공백일 경우")
    class ContextEmptyReason {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("400 Badrequest으로 응답한다.")
      void ItResponseBadRequest(String reason) throws Exception {
        // given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("reason", reason);
        requestMap.put("fromUserId", BASE_FROM_USER_ID);
        requestMap.put("toUserId", BASE_TO_USER_ID);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL)
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
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("reason", BASE_REASON);
        requestMap.put("fromUserId", BASE_FROM_USER_ID);
        requestMap.put("toUserId", BASE_TO_USER_ID);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        long createdReportId = 1L;

        given(reportService.create(anyLong(), any(ReportCreateRequest.class)))
            .willReturn(ReportCreateResponse.from(createdReportId));

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response
            .andExpect(status().isCreated())
            .andDo(
                document(
                    "Create report",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("reason")
                            .type(JsonFieldType.STRING)
                            .description("신고 이유"),
                        fieldWithPath("fromUserId")
                            .type(JsonFieldType.NUMBER)
                            .description("신고자 식별자"),
                        fieldWithPath("toUserId")
                            .type(JsonFieldType.NUMBER)
                            .description("피신고자 식별자"),
                        fieldWithPath("type")
                            .type(JsonFieldType.STRING)
                            .description("신고 객체 종류(옵션)")
                            .optional(),
                        fieldWithPath("type")
                            .type(JsonFieldType.NUMBER)
                            .description("신고 객체 식별자")
                            .optional()
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
}
