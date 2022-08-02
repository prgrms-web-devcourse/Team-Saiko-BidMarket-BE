package com.saiko.bidmarket.common.oauth2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.saiko.bidmarket.common.jwt.Jwt;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.service.UserService;

public class OAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private final Jwt jwt;

  private final UserService userService;

  public OAuth2AuthenticationSuccessHandler(Jwt jwt, UserService userService) {

    this.jwt = jwt;
    this.userService = userService;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication)
      throws
      IOException {

    if (authentication instanceof OAuth2AuthenticationToken) {
      OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken)authentication;
      OAuth2User principal = oauth2Token.getPrincipal();
      log.debug("Message {}, {}", principal.getName(), principal.getAttributes());
      String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

      User user = processUserOAuth2UserJoin(principal, registrationId);
      String loginSuccessJson = generateLoginSuccessJson(user);
      response.setContentType("application/json;charset=UTF-8");
      response.setContentLength(loginSuccessJson.getBytes(StandardCharsets.UTF_8).length);
      response.sendRedirect("https://bidmarket.vercel.app/auth?" + loginSuccessJson);
    }
  }

  private User processUserOAuth2UserJoin(OAuth2User oAuth2User, String registrationId) {
    return userService.join(oAuth2User, registrationId);
  }

  private String generateLoginSuccessJson(User user) {
    String token = generateToken(user);
    log.debug("Jwt({}) created for oauth2 login user {}", token, user.getId());
    return "token=" + token;
  }

  private String generateToken(User user) {
    return jwt.sign(Jwt.Claims.from(user.getId(), new String[]{"ROLE_USER"}));
  }
}
