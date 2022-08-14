package com.saiko.bidmarket.report.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.report.entity.Report;
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

  private static final long typeId = 1L;

  private static final String reason = "신고 이유";

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
        String requestBody = createRequestBody(
            Report.Type.PRODUCT.name(),
            typeId,
            reason
        );

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
    @DisplayName("타입이 비었거나 공백일 경우")
    class ContextEmptyType {

      @ParameterizedTest
      @NullAndEmptySource
      @DisplayName("400 Badrequest으로 응답한다.")
      void ItResponseBadRequest(String type) throws Exception {
        // given

        String requestBody = createRequestBody(type, typeId, reason);

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
    @DisplayName("타입Id가 숫자가 아닐 경우")
    class ContextNotNumberTypeId {

      @ParameterizedTest
      @ValueSource(strings = {"NotNumber"})
      @DisplayName("400 Badrequest으로 응답한다.")
      void ItResponseBadRequest(String typeId) throws Exception {
        // given

        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("type", Report.Type.USER.name());
        requestMap.put("typeId", typeId);
        requestMap.put("reason", reason);

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
    @DisplayName("타입 Id로 객체를 찾을 수 없을 경우")
    class ContextEmptyTypeId {

      @ParameterizedTest
      @ValueSource(longs = {Long.MIN_VALUE, -1, 0, 1})
      @DisplayName("404 NotFound로 응답한다.")
      void ItResponseNotFound(Long typeId) throws Exception {
        // given
        String requestBody = createRequestBody(
            Report.Type.USER.name(),
            typeId,
            reason
        );
        doThrow(NotFoundException.class)
            .when(reportService)
            .create(anyLong(), any());

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        response.andExpect(status().isNotFound());
      }
    }

    @Nested
    @DisplayName("정상적인 요청이 들어올 경우")
    class ContextValidRequest {

      @ParameterizedTest
      @EnumSource
      @DisplayName("201 Created를 응답한다.")
      void ItResponseForbidden(Report.Type type) throws Exception {
        // given
        String requestBody = createRequestBody(type.name(), typeId, reason);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        );

        // then
        verify(reportService, atLeastOnce()).create(anyLong(), any());
        response
            .andExpect(status().isCreated())
            .andDo(
                document(
                    "Create report to user",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("type")
                            .type(JsonFieldType.STRING)
                            .description("신고 객체 유형"),
                        fieldWithPath("typeId")
                            .type(JsonFieldType.NUMBER)
                            .description("신고 객체 식별자"),
                        fieldWithPath("reason")
                            .type(JsonFieldType.STRING)
                            .description("신고 이유")
                    )
                )
            );

      }
    }
  }

  private String createRequestBody(
      String type,
      Long typeId,
      String reason
  ) throws JsonProcessingException {
    HashMap<String, Object> requestMap = new HashMap<>();
    requestMap.put("type", type);
    requestMap.put("typeId", typeId);
    requestMap.put("reason", reason);

    return objectMapper.writeValueAsString(requestMap);
  }
}
