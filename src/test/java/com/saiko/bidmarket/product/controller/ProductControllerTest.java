package com.saiko.bidmarket.product.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.lang.reflect.Constructor;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.service.ProductService;

@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
@WebMvcTest(
    controllers = ProductController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebSecurityConfigurer.class)
    },
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
    }
)
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerTest {

  private static final String API_URL = "/api/v1/products";

  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ProductService productService;

  @BeforeEach
  public void setUp(WebApplicationContext webApplicationContext,
                    RestDocumentationContextProvider restDocumentationContextProvider) {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))
        .apply(documentationConfiguration(restDocumentationContextProvider))
        .build();
  }

  @Nested
  @DisplayName("findById 메소드는")
  class DescribeFindById {

    @Nested
    @DisplayName("id에 숫자 외에 다른 문자가 들어온다면")
    class ContextNotNumberId {

      @Test
      @DisplayName("BadRequest로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given
        String inputId = "NotNumber";

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.get(API_URL + "/{id}", inputId));

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("id에 음수가 들어온다면")
    class ContextNegativeNumberId {

      @Test
      @DisplayName("BadRequest로 응답한다.")
      void itResponseBadRequest() throws Exception {
        // given
        long inputId = -1;

        given(productService.findById(anyLong())).willThrow(IllegalArgumentException.class);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.get(API_URL + "/{id}", inputId));

        // then
        response.andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("id에 해당하는 상품이 없을 경우 ")
    class ContextNotFoundProductById {

      @Test
      @DisplayName("NotFound로 응답한다.")
      void itResponseNotFound() throws Exception {
        // given
        long inputId = 1;

        given(productService.findById(anyLong())).willThrow(NotFoundException.class);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.get(API_URL + "/{id}", inputId));

        // then
        response.andExpect(status().isNotFound());
      }
    }

    @Nested
    @DisplayName("정상적인 id가 들어온다면")
    class ContextValidId {

      @Test
      @DisplayName("해당 id를 가진 상품의 도메인 객체와 OK로 응답한다.")
      void itResponseOkWithProductDomainObjectHasInputId() throws Exception {
        // given
        long inputId = 1;
        String title = "상품 타이틀";
        String description = "상품 설명";
        int minimumPrice = 10_000;
        Category category = Category.DIGITAL_DEVICE;
        String location = "어디어디";

        int year = 2022;
        int month = 7;
        int day = 20;
        int hour = 21;
        int minute = 46;
        int second = 0;

        int daysOfWeek = 7;

        LocalDateTime createdAt = LocalDateTime.of(year, month, day, hour, minute, second);
        LocalDateTime updatedAt = LocalDateTime.of(year, month, day, hour, minute, second);
        LocalDateTime expireAt = createdAt.plusDays(daysOfWeek);

        Class<Product> productClass = Product.class;
        Constructor<Product> productConstructor = productClass.getDeclaredConstructor();
        productConstructor.setAccessible(true);
        Product foundProduct = productConstructor.newInstance();

        ReflectionTestUtils.setField(foundProduct, "id", inputId);
        ReflectionTestUtils.setField(foundProduct, "title", title);
        ReflectionTestUtils.setField(foundProduct, "description", description);
        ReflectionTestUtils.setField(foundProduct, "minimumPrice", minimumPrice);
        ReflectionTestUtils.setField(foundProduct, "category", category);
        ReflectionTestUtils.setField(foundProduct, "location", location);
        ReflectionTestUtils.setField(foundProduct, "expireAt", expireAt);
        ReflectionTestUtils.setField(foundProduct, "createdAt", createdAt);
        ReflectionTestUtils.setField(foundProduct, "updatedAt", updatedAt);

        given(productService.findById(anyLong())).willReturn(foundProduct);

        // when
        ResultActions response = mockMvc.perform(
            RestDocumentationRequestBuilders.get(API_URL + "/{id}", inputId));

        // then
        verify(productService,atLeastOnce()).findById(anyLong());
        response.andExpect(status().isOk())
                .andDo(document("Find Product by id",
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER)
                                                       .description("상품 식별자"),
                                    fieldWithPath("title").type(JsonFieldType.STRING)
                                                          .description("상품 제목"),
                                    fieldWithPath("description").type(JsonFieldType.STRING)
                                                                .description("상품 소개"),
                                    fieldWithPath("minimumPrice").type(JsonFieldType.NUMBER)
                                                                 .description("최소주문금액"),
                                    fieldWithPath("categoryName").type(JsonFieldType.STRING)
                                                                 .description("카테고리 이름"),
                                    fieldWithPath("location").type(JsonFieldType.STRING)
                                                             .description("거래 위치"),
                                    fieldWithPath("expireAt").type(JsonFieldType.STRING)
                                                             .description("비딩 종료 시간"),
                                    fieldWithPath("createdAt").type(JsonFieldType.STRING)
                                                              .description("생성 시간"),
                                    fieldWithPath("updatedAt").type(JsonFieldType.STRING)
                                                              .description("수정 시간")
                                )
                ));
      }
    }
  }
}