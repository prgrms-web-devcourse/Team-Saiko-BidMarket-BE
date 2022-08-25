package com.saiko.bidmarket.comment.controller;

import static com.saiko.bidmarket.common.Sort.*;
import static com.saiko.bidmarket.product.Category.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectResponse;
import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.comment.service.CommentService;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.entity.UserRole;
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

  private static User writer = User
      .builder()
      .username("제로")
      .profileImage("image")
      .provider("google")
      .providerId("123")
      .userRole(UserRole.ROLE_USER)
      .build();
  private static Product product = Product
      .builder()
      .title("귤 팔아요")
      .description("맛있어요")
      .category(FOOD)
      .images(List.of("image1"))
      .location("제주도")
      .minimumPrice(1000)
      .writer(writer)
      .build();

  private static Comment comment = Comment
      .builder()
      .writer(writer)
      .product(product)
      .content("그냥 주세요")
      .build();
  private static long writerId = 1;
  private static long productId = 1;
  private static long commentId = 1;

  @BeforeAll
  static void setup() {
    ReflectionTestUtils.setField(writer, "id", writerId);
    ReflectionTestUtils.setField(product, "id", productId);
    ReflectionTestUtils.setField(comment, "id", commentId);
    ReflectionTestUtils.setField(comment, "createdAt", LocalDateTime.now());
  }

  @Nested
  @DisplayName("create 메서드는")
  @WithMockCustomLoginUser
  class DescribeCreate {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidData {
      @Test
      @DisplayName("댓글을 저장하고 댓글의 id 값을 반환한다")
      void ItSaveCommentAndReturnId() throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("productId", 1);
        requestMap.put("content", "직거래도 돼요?");

        String requestBody = objectMapper.writeValueAsString(requestMap);

        given(commentService.create(anyLong(), any(CommentCreateRequest.class)))
            .willReturn(CommentCreateResponse.from(1));

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        verify(commentService).create(anyLong(), any(CommentCreateRequest.class));
        response
            .andExpect(status().isCreated())
            .andDo(document(
                "Create comment",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("productId")
                        .type(JsonFieldType.NUMBER)
                        .description("상품 아이디"),
                    fieldWithPath("content")
                        .type(JsonFieldType.STRING)
                        .description("댓글 내용")
                ),
                responseFields(
                    fieldWithPath("id")
                        .type(JsonFieldType.NUMBER)
                        .description("댓글 아이디")
                )
            ));
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
  }

  @Nested
  @DisplayName("findAll 메서드는")
  @WithMockCustomLoginUser
  class DescribeFindAll {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidData {
      @Test
      @DisplayName("상품의 댓글을 조회하고 결과를 반환한다")
      void ItReturnCommentList() throws Exception {
        //given
        given(commentService.findAllByProduct(any(CommentSelectRequest.class)))
            .willReturn(List.of(CommentSelectResponse.from(comment)));

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(BASE_URL)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .queryParam("productId", String.valueOf(productId))
            .queryParam("sort", CREATED_AT_ASC.name());

        ResultActions response = mockMvc.perform(request);

        //then
        verify(commentService).findAllByProduct(any(CommentSelectRequest.class));
        response
            .andExpect(status().isOk())
            .andDo(document("Select comment", preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParameters(
                                parameterWithName("productId").description("상품 아이디"),
                                parameterWithName("sort")
                                    .description("댓글 정렬 기준")
                                    .optional()
                            ),
                            responseFields(
                                fieldWithPath("[].commentId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("댓글 식별자"),
                                fieldWithPath("[].userId")
                                    .type(JsonFieldType.NUMBER)
                                    .description("유저 식별자"),
                                fieldWithPath("[].username")
                                    .type(JsonFieldType.STRING)
                                    .description("유저 이름"),
                                fieldWithPath("[].profileImage")
                                    .type(JsonFieldType.STRING)
                                    .description("유저 프로필 이미지"),
                                fieldWithPath("[].content")
                                    .type(JsonFieldType.STRING)
                                    .description("댓글 내용"),
                                fieldWithPath("[].createdAt")
                                    .type(JsonFieldType.STRING)
                                    .description("생성 시간"),
                                fieldWithPath("[].updatedAt")
                                    .type(JsonFieldType.STRING)
                                    .description("수정 시간")
                                    .optional()
                            )
            ));
      }
    }

    @Nested
    @DisplayName("sort 가 지원하지 않는 정렬 조건이라면")
    class ContextUnsupportedSort {

      @Test
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given
        String unsupportedSort = "unsupportedSort";

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .get(BASE_URL)
                                                     .queryParam(
                                                         "productId",
                                                         "1"
                                                     )
                                                     .queryParam(
                                                         "sort",
                                                         unsupportedSort
                                                     ));
        // then
        response.andExpect(status().isBadRequest());
      }
    }
  }
}
