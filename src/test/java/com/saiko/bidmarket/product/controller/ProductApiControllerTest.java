package com.saiko.bidmarket.product.controller;

import static com.saiko.bidmarket.product.Category.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
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
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.service.ProductService;
import com.saiko.bidmarket.util.ControllerSetUp;

@WebMvcTest(controllers = ProductApiController.class, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)},
    excludeAutoConfiguration = {SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class})
@MockBeans({@MockBean(JpaMetamodelMappingContext.class)})
class ProductApiControllerTest extends ControllerSetUp {
  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ProductService productService;

  public static final String CREATE_URL = "/api/v1/products";

  static class TitleSourceOutOfRange implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of((Object)null),
          Arguments.of(""),
          Arguments.of("\t"),
          Arguments.of("\n"),
          Arguments.of("a".repeat(17))
      );
    }
  }

  static class DescriptionSourceOutOfRange implements ArgumentsProvider {
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
  class DescribeCreate {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidData {
      @Test
      @DisplayName("상품을 저장하고 상품의 id 값을 반환한다")
      void ItSaveProduct() throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", "키보드팝니다");
        requestMap.put("description", "깨끗합니다");
        requestMap.put("category", DIGITAL_DEVICE);
        requestMap.put("minimumPrice", 10000);
        requestMap.put("location", "관악구 신림동");
        requestMap.put("images", new String[]{"imageUrl1, imageUrl2"});

        String requestBody = objectMapper.writeValueAsString(requestMap);

        given(productService.create(any(ProductCreateRequest.class)))
            .willReturn(1L);

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(CREATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        verify(productService).create(any(ProductCreateRequest.class));
        response.andExpect(status().isCreated())
                .andDo(document("Create product",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING)
                                                          .description("제목"),
                                    fieldWithPath("description").type(JsonFieldType.STRING)
                                                                .description("설명"),
                                    fieldWithPath("images").type(JsonFieldType.ARRAY)
                                                           .description("상품 이미지"),
                                    fieldWithPath("category").type(JsonFieldType.STRING)
                                                             .description("카테고리"),
                                    fieldWithPath("minimumPrice").type(JsonFieldType.NUMBER)
                                                                 .description("최소 가격"),
                                    fieldWithPath("location").type(JsonFieldType.STRING)
                                                             .description("희망 거래 장소")),
                                responseFields(
                                    fieldWithPath("id")
                                        .type(JsonFieldType.NUMBER)
                                        .description("상품 아이디")
                                )));
      }
    }

    @Nested
    @DisplayName("title 의 길이가 범위를 벗어나면")
    class ContextWithTitleOutOfRange {

      @ParameterizedTest
      @ArgumentsSource(TitleSourceOutOfRange.class)
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest(String title) throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", title);
        requestMap.put("description", "깨끗합니다");
        requestMap.put("category", DIGITAL_DEVICE);
        requestMap.put("minimumPrice", 10000);
        requestMap.put("location", "관악구 신림동");
        requestMap.put("images", new String[]{"imageUrl1, imageUrl2"});

        String requestBody = objectMapper.writeValueAsString(requestMap);

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(CREATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("description 의 길이가 범위를 벗어나면")
    class ContextWithDescriptionOutOfRange {

      @ParameterizedTest
      @ArgumentsSource(DescriptionSourceOutOfRange.class)
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest(String description) throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", "키보드팝니다");
        requestMap.put("description", description);
        requestMap.put("category", DIGITAL_DEVICE);
        requestMap.put("minimumPrice", 10000);
        requestMap.put("location", "관악구 신림동");
        requestMap.put("imageUrl1", new String[]{"imageUrl1, imageUrl2"});

        String requestBody = objectMapper.writeValueAsString(requestMap);

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(CREATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("images 의 길이가 범위를 벗어나면")
    class ContextWithImagesOutOfRange {

      @Test
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest() throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", "키보드팝니다");
        requestMap.put("description", "깨끗합니다");
        requestMap.put("category", DIGITAL_DEVICE);
        requestMap.put("minimumPrice", 10000);
        requestMap.put("location", "관악구 신림동");
        requestMap.put("images", Arrays.asList("imageUrl1",
                                               "imageUrl2",
                                               "imageUrl3",
                                               "imageUrl4",
                                               "imageUrl5",
                                               "imageUrl6"));

        String requestBody = objectMapper.writeValueAsString(requestMap);

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(CREATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("minimumPrice 가 범위를 벗어나면")
    class ContextWithMinimumPriceOutOfRange {

      @Test
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest() throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", "키보드팝니다");
        requestMap.put("description", "깨끗합니다");
        requestMap.put("category", DIGITAL_DEVICE);
        requestMap.put("minimumPrice", 100);
        requestMap.put("location", "관악구 신림동");
        requestMap.put("imageUrl1", new String[]{"imageUrl1, imageUrl2"});

        String requestBody = objectMapper.writeValueAsString(requestMap);

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(CREATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("category 가 null 이라면")
    class ContextWithCategoryNull {

      @Test
      @DisplayName("BadRequest 를 응답한다")
      void ItResponseBadRequest() throws Exception {
        //given
        HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("title", "키보드팝니다");
        requestMap.put("description", "깨끗합니다");
        requestMap.put("minimumPrice", 10000);
        requestMap.put("location", "관악구 신림동");
        requestMap.put("imageUrl1", new String[]{"imageUrl1, imageUrl2"});

        String requestBody = objectMapper.writeValueAsString(requestMap);

        //when
        MockHttpServletRequestBuilder request = RestDocumentationRequestBuilders
            .post(CREATE_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andExpect(status().isBadRequest());
      }
    }
  }
}