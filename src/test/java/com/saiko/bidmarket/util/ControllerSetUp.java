package com.saiko.bidmarket.util;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.saiko.bidmarket.common.config.WebSecurityConfig;
import com.saiko.bidmarket.user.service.UserService;

@Import({WebSecurityConfig.class})
@MockBeans({
    @MockBean(JpaMetamodelMappingContext.class),
    @MockBean(JdbcOperations.class),
    @MockBean(UserService.class)
})
@ExtendWith(RestDocumentationExtension.class)
public class ControllerSetUp {

  protected MockMvc mockMvc;

  @BeforeEach
  public void setUp(WebApplicationContext webApplicationContext,
                    RestDocumentationContextProvider restDocumentationContextProvider) {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(webApplicationContext)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))
        .apply(springSecurity())
        .apply(documentationConfiguration(restDocumentationContextProvider))
        .build();
  }
}
