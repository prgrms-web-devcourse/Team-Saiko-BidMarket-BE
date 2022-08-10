package com.saiko.bidmarket.notification.controller;

import static com.saiko.bidmarket.notification.NotificationType.*;
import static com.saiko.bidmarket.product.Category.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;
import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.dto.NotificationRepoDto;
import com.saiko.bidmarket.notification.service.NotificationService;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = NotificationApiController.class)
public class NotificationApiControllerTest extends ControllerSetUp {
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private NotificationService notificationService;

  public static final String BASE_URL = "/api/v1/notifications";

  @Nested
  @DisplayName("getAllNotification 메소드는")
  @WithMockCustomLoginUser
  class DescribeGetAllNotification {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidData {

      @Test
      @DisplayName("유저의 모든 알림을 조회하고 결과를 반환한다")
      void ItResponseNotificationList() throws Exception {
        //given
        User writer = User
            .builder()
            .username("제로")
            .profileImage("image")
            .provider("google")
            .providerId("123")
            .group(new Group())
            .build();
        ReflectionTestUtils.setField(writer, "id", 1L);

        User user = User
            .builder()
            .username("마틴")
            .profileImage("image")
            .provider("google")
            .providerId("123")
            .group(new Group())
            .build();
        ReflectionTestUtils.setField(user, "id", 2L);

        Product product = Product
            .builder()
            .title("귤 팔아요")
            .description("맛있어요")
            .category(FOOD)
            .images(List.of("image1"))
            .location("제주도")
            .minimumPrice(1000)
            .writer(writer)
            .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        Notification notification = Notification
            .builder()
            .user(user)
            .type(END_PRODUCT_FOR_BIDDER)
            .product(product)
            .build();
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());

        NotificationRepoDto notificationRepoDto = new NotificationRepoDto(1L, product.getId(),
                                                                          product.getTitle(),
                                                                          product.getThumbnailImage(),
                                                                          notification.getType(),
                                                                          notification.getCreatedAt(),
                                                                          notification.getUpdatedAt()
        );

        NotificationSelectResponse notificationSelectResponse = NotificationSelectResponse.from(
            notificationRepoDto);

        given(notificationService.findAllNotifications(
            any(UnsignedLong.class),
            any(NotificationSelectRequest.class)
        ))
            .willReturn(List.of(notificationSelectResponse));

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .get(BASE_URL)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .queryParam("offset", "0")
            .queryParam("limit", "1");

        ResultActions response = mockMvc.perform(request);

        //then
        response
            .andExpect(status().isOk())
            .andDo(document("Select notification", preprocessRequest(
                prettyPrint()), preprocessResponse(prettyPrint()), requestParameters(
                parameterWithName("offset").description("알림 조회 시작 번호"),
                parameterWithName("limit").description("알림 조회 개수")
            ), responseFields(
                fieldWithPath("[].id")
                    .type(JsonFieldType.NUMBER)
                    .description("알림 식별자"),
                fieldWithPath("[].productId")
                    .type(JsonFieldType.NUMBER)
                    .description("상품 식별자"),
                fieldWithPath("[].title")
                    .type(JsonFieldType.STRING)
                    .description("상품 제목"),
                fieldWithPath("[].thumbnailImage")
                    .type(JsonFieldType.STRING)
                    .description("상품 썸네일 이미지"),
                fieldWithPath("[].type")
                    .type(JsonFieldType.STRING)
                    .description("알림 타입"),
                fieldWithPath("[].content")
                    .type(JsonFieldType.STRING)
                    .description("알림 메세지"),
                fieldWithPath("[].createdAt")
                    .type(JsonFieldType.STRING)
                    .description("알림 생성 시간"),
                fieldWithPath("[].updatedAt")
                    .type(JsonFieldType.STRING)
                    .description("알림 수정 시간")
                    .optional()
            )));
      }
    }

    @Nested
    @DisplayName("offset 에 숫자 외에 다른 문자가 들어온다면")
    class ContextNotNumberOffset {

      @Test
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given
        String offset = "NotNumber";

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(BASE_URL)
                .param("offset", offset));

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("offset 에 음수가 들어온다면")
    class ContextNegativeNumberOffset {

      @Test
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(BASE_URL)
                .param("offset", "-1"));

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("limit 에 숫자 외에 다른 문자가 들어온다면")
    class ContextNotNumberLimit {

      @Test
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given
        String limit = "NotNumber";

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(BASE_URL)
                .param("offset", "1")
                .param("limit", limit)
        );

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("limit 에 양수가 아닌 숫자가 들어오면")
    class ContextNegativeOrZeroNumberLimit {

      @ParameterizedTest
      @ValueSource(strings = {"0", "-1"})
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest(String limit) throws Exception {
        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders
                .get(BASE_URL)
                .param("offset", "1")
                .param("limit", limit)
        );
        // then
        response.andExpect(status().isBadRequest());
      }
    }
  }
}
