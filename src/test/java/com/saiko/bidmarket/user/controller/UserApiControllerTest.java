package com.saiko.bidmarket.user.controller;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.user.controller.dto.UserUpdateRequest;
import com.saiko.bidmarket.user.service.UserService;
import com.saiko.bidmarket.util.ControllerSetUp;
import com.saiko.bidmarket.util.WithMockCustomLoginUser;

@WebMvcTest(controllers = UserApiController.class)
class UserApiControllerTest extends ControllerSetUp {

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  public static final String BASE_URL = "/api/v1/users";

  @Nested
  @DisplayName("updateUser 메서드는")
  @WithMockCustomLoginUser
  class DescribeUpdateUser {

    @Nested
    @DisplayName("유저 닉네임, 이미지의 수정 정보를 받으면")
    class ContextValidNickNameAndImage {

      @Test
      @DisplayName("Service의 updateUser메서드를 호출하고 OK를 반환한다.")
      void itReturnOkAndCallServiceMethod() throws Exception {
        //given
        final UserUpdateRequest requestDto = new UserUpdateRequest("test", "test");
        final String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        final MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .patch(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        verify(userService).updateUser(anyLong(), any(UserUpdateRequest.class));
        response
            .andExpect(status().isOk())
            .andDo(document("Update User",
                            preprocessRequest(prettyPrint()),
                            requestFields(
                                fieldWithPath("username").type(JsonFieldType.STRING)
                                                         .description("변경할 유저 이름"),
                                fieldWithPath("image").type(JsonFieldType.STRING)
                                                      .description("변경할 유저 프로필 이미지")
                            )
            ));
      }
    }

    @Nested
    @DisplayName("이미지, 유저 닉네임을 모두 비어있는 인자로 받으면")
    class ContextBlankUserName {

      @Test
      @DisplayName("400 BadRequest를 반환한다.")
      void itThrow400BadRequest() throws Exception {
        //given
        final String nullSource = null;
        final UserUpdateRequest requestDto = new UserUpdateRequest(nullSource, nullSource);
        final String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        final MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .patch(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response
            .andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("20자가 넘는 닉네임을 인자로 받으면")
    class ContextOverLengthUserName {

      @Test
      @DisplayName("400 BadRequest를 반환한다.")
      void itThrow400BadRequest() throws Exception {
        //given
        final String overLengthUsername = "testtesttesttesttesttestname";
        final UserUpdateRequest requestDto = new UserUpdateRequest(overLengthUsername, null);
        final String requestBody = objectMapper.writeValueAsString(requestDto);

        //when
        final MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .patch(BASE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response
            .andExpect(status().isBadRequest());
      }
    }
  }

}