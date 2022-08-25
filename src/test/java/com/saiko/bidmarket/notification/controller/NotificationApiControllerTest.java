package com.saiko.bidmarket.notification.controller;

import static com.saiko.bidmarket.notification.NotificationType.*;
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
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectRequest;
import com.saiko.bidmarket.notification.controller.dto.NotificationSelectResponse;
import com.saiko.bidmarket.notification.entity.Notification;
import com.saiko.bidmarket.notification.repository.dto.NotificationRepoDto;
import com.saiko.bidmarket.notification.service.NotificationService;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.entity.UserRole;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = NotificationApiController.class)
public class NotificationApiControllerTest extends ControllerSetUp {
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private NotificationService notificationService;

  public static final String BASE_URL = "/api/v1/notifications";

  private User user(String name) {
    return User
        .builder()
        .username(name)
        .profileImage("imageURL")
        .provider("provider")
        .providerId("providerId")
        .userRole(UserRole.ROLE_USER)
        .build();
  }

  private Product product(
      User writer,
      int minimumPrice
  ) {
    return Product
        .builder()
        .title("title")
        .description("description")
        .images(List.of("image"))
        .minimumPrice(minimumPrice)
        .writer(writer)
        .category(Category.ETC)
        .build();
  }

  private Notification notification(User user, Product product) {
    return Notification
        .builder()
        .user(user)
        .product(product)
        .type(
            END_PRODUCT_FOR_WRITER_WITH_WINNER)
        .build();
  }

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
        User writer = user("제로");
        ReflectionTestUtils.setField(writer, "id", 1L);

        User user = user("마틴");
        ReflectionTestUtils.setField(user, "id", 2L);

        Product product = product(writer, 1000);
        ReflectionTestUtils.setField(product, "id", 1L);

        Notification notification = notification(user, product);
        ReflectionTestUtils.setField(notification, "createdAt", LocalDateTime.now());

        NotificationRepoDto notificationRepoDto = new NotificationRepoDto(
            1L,
            product.getId(),
            product.getTitle(),
            product.getThumbnailImage(),
            notification.getType(),
            notification.isChecked(),
            notification.getCreatedAt(),
            notification.getUpdatedAt());

        NotificationSelectResponse notificationSelectResponse = NotificationSelectResponse.from(
            notificationRepoDto);

        given(notificationService.findAllNotifications(
            anyLong(),
            any(NotificationSelectRequest.class)
        )).willReturn(List.of(notificationSelectResponse));

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
                fieldWithPath("[].checked")
                    .type(JsonFieldType.BOOLEAN)
                    .description("알림 조회 여부"),
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
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
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
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
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
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .get(BASE_URL)
                                                     .param("offset", "1")
                                                     .param("limit", limit));

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
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .get(BASE_URL)
                                                     .param("offset", "1")
                                                     .param("limit", limit));

        // then
        response.andExpect(status().isBadRequest());
      }
    }
  }

  @Nested
  @DisplayName("checkNotification 메소드는")
  @WithMockCustomLoginUser
  class DescribeCheckNotification {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidData {

      @Test
      @DisplayName("유저의 알림 조회 상태를 변경한다")
      void ItCheckNotification() throws Exception {
        //given
        long notificationId = 1L;

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .put(BASE_URL + "/{id}", notificationId)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResultActions response = mockMvc.perform(request);

        //then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "Check notification",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(parameterWithName("id").description("알림 아이디"))
            ));
      }
    }

    @Nested
    @DisplayName("notificationId 에 숫자 외에 다른 문자가 들어온다면")
    class ContextNotNumberNotificationId {

      @Test
      @DisplayName("BadRequest 로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given
        String notificationId = "NotNumber";

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.put(
            BASE_URL + "/{id}", notificationId));

        // then
        response.andExpect(status().isBadRequest());
      }
    }
  }
}
