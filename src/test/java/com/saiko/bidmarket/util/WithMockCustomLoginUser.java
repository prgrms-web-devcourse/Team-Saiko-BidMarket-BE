package com.saiko.bidmarket.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomLoginUserSecurityContextFactory.class)
public @interface WithMockCustomLoginUser {
}
