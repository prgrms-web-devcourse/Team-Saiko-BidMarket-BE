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
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.service.ReportService;
import com.saiko.bidmarket.report.service.dto.ReportCreateDto;
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
    @DisplayName("토큰 속 유저와 신고자가 다를 경우")
    class ContextNotMatchedUserWithRequestAndToken {

      @Test
      @DisplayName("401 Forbidden으로 응답한다.")
      void ItResponseForbidden() throws Exception {
        // given
        final long otherFromUserId = Long.MAX_VALUE - BASE_FROM_USER_ID;

        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("reason", BASE_REASON);
        requestMap.put("fromUserId", otherFromUserId);
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
        verify(reportService, never()).create(any(ReportCreateDto.class));
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("신고자와 피신고자가 같은 경우")
    class ContextSameFromUserAndToUser {

      @Test
      @DisplayName("400 BadRequest으로 응답한다.")
      void ItResponseBadRequest() throws Exception {
        // given
        final long otherFromUserId = Long.MAX_VALUE - BASE_FROM_USER_ID;

        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("reason", BASE_REASON);
        requestMap.put("fromUserId", otherFromUserId);
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
        verify(reportService, never()).create(any(ReportCreateDto.class));
        response.andExpect(status().isBadRequest());
      }
    }

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
        verify(reportService, never()).create(any(ReportCreateDto.class));
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("피신고자를 찾을 수 없는 경우")
    class ContextNotFoundToUser {

      @Test
      @DisplayName("404 NotFound로 응답한다.")
      void ItResponseNotFound() throws Exception {
        // given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("reason", BASE_REASON);
        requestMap.put("fromUserId", BASE_FROM_USER_ID);
        requestMap.put("toUserId", BASE_TO_USER_ID);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        given(reportService.create(any())).willThrow(NotFoundException.class);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        verify(reportService, atLeastOnce()).create(any(ReportCreateDto.class));
        response.andExpect(status().isNotFound());
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

        given(reportService.create(any(ReportCreateDto.class))).willReturn(1L);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        verify(reportService, atLeastOnce()).create(any(ReportCreateDto.class));
        response
            .andExpect(status().isCreated())
            .andDo(
                document(
                    "Create report",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("reason").description("신고 이유"),
                        fieldWithPath("fromUserId").description("신고자 식별자"),
                        fieldWithPath("toUserId").description("피신고자 식별자")),
                    responseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("신고 식별자"))
                )
            );

      }
    }
  }
}