package com.saiko.bidmarket.common.oauth2;

import static java.util.Optional.*;

import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

public class HttpCookieOAuth2AuthorizationRequestRepository implements
    AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  private static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME =
      "OAUTH2_AUTHORIZATION_REQUEST";

  private final String cookieName;

  private final int cookieExpireSeconds;

  public HttpCookieOAuth2AuthorizationRequestRepository() {

    this(OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, 180);
  }

  public HttpCookieOAuth2AuthorizationRequestRepository(
      String cookieName,
      int cookieExpireSeconds) {

    this.cookieName = cookieName;
    this.cookieExpireSeconds = cookieExpireSeconds;
  }

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {

    return getCookie(request)
        .map(this::getOAuth2AuthorizationRequest)
        .orElse(null);
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {

    if (authorizationRequest == null) {
      getCookie(request).ifPresent(cookie -> clear(cookie, response));
    } else {
      String value = Base64.getUrlEncoder()
                           .encodeToString(SerializationUtils.serialize(authorizationRequest));
      Cookie cookie = new Cookie(cookieName, value);
      cookie.setPath("/");
      cookie.setHttpOnly(true);
      cookie.setMaxAge(cookieExpireSeconds);
      response.addCookie(cookie);
    }
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {

    return loadAuthorizationRequest(request);
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                               HttpServletResponse response) {

    return getCookie(request)
        .map(cookie -> {
          OAuth2AuthorizationRequest oauth2Request = getOAuth2AuthorizationRequest(cookie);
          clear(cookie, response);
          return oauth2Request;
        })
        .orElse(null);
  }

  private Optional<Cookie> getCookie(HttpServletRequest request) {

    return ofNullable(WebUtils.getCookie(request, cookieName));
  }

  private void clear(Cookie cookie, HttpServletResponse response) {

    cookie.setValue("");
    cookie.setPath("/");
    cookie.setMaxAge(0);
    response.addCookie(cookie);
  }

  private OAuth2AuthorizationRequest getOAuth2AuthorizationRequest(Cookie cookie) {

    return SerializationUtils.deserialize(
        Base64.getUrlDecoder().decode(cookie.getValue())
    );
  }
}
