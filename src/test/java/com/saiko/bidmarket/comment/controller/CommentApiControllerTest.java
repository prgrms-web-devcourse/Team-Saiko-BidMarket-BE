package com.saiko.bidmarket.comment.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.anyLong;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.service.CommentService;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = CommentApiController.class)
class CommentApiControllerTest extends ControllerSetUp {
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CommentService commentService;

  public static final String BASE_URL = "/api/v1/comments";

  static class ContentSourceOutOfRange implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of((Object)null),
          Arguments.of(""),
          Arguments.of("\t"),
          Arguments.of("\n"),
          Arguments.of("a".repeat(501))
      );
    }
  }

  @Nested
  @DisplayName("create 메서드는")
  @WithMockCustomLoginUser
  class DescribeCreate {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidData {
      @Test
      @DisplayName("상품을 저장하고 상품의 id 값을 반환한다")
      void ItSaveCommentAndReturnId() throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("productId", 1);
        requestMap.put("content", "직거래도 돼요?");

        String requestBody = objectMapper.writeValueAsString(requestMap);

        given(commentService.create(anyLong(), any(CommentCreateRequest.class)))
            .willReturn(new CommentCreateResponse(UnsignedLong.valueOf(1)));

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        verify(commentService).create(anyLong(), any(CommentCreateRequest.class));
        response.andExpect(status().isCreated())
                .andDo(document("Create comment",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                    fieldWithPath("productId").type(JsonFieldType.NUMBER)
                                                              .description("상품 아이디"),
                                    fieldWithPath("content").type(JsonFieldType.STRING)
                                                            .description("댓글 내용")),
                                responseFields(
                                    fieldWithPath("id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("댓글 아이디")
                                )));
      }
    }

    @Nested
    @DisplayName("content 의 길이가 범위를 벗어나면")
    class ContextWithContentOutOfRange {

      @ParameterizedTest
      @ArgumentsSource(ContentSourceOutOfRange.class)
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest(String content) throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("productId", 1);
        requestMap.put("content", content);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("productId 에 숫자 외에 다른 문자가 들어온다면")
    class ContextNotNumberProductId {

      @Test
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given
        String productId = "NotNumber";

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.post(BASE_URL)
                                            .param("productId", productId)
                                            .param("content", "제가 살래요")
        );

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("productId 에 양수가 아닌 숫자가 들어오면")
    class ContextNegativeOrZeroNumberProductId {

      @ParameterizedTest
      @ValueSource(strings = {"0", "-1"})
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest(String productId) throws Exception {
        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.post(BASE_URL)
                                            .param("productId", productId)
                                            .param("content", "깨끗한가요?")
        );
        // then
        response.andExpect(status().isBadRequest());
      }
    }
  }
}