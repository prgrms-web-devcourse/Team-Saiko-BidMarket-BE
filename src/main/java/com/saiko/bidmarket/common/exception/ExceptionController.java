package com.saiko.bidmarket.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Order(1)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    logger.warn("MethodArgumentNotValidException : ", e);
  }

  @Order(2)
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleIllegalArgumentException(IllegalArgumentException e) {
    logger.warn("IllegalArgumentException : ", e);
  }

  @Order(3)
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handleNotFoundException(NotFoundException e) {
    logger.warn("NotFoundException : ", e);
  }

  @Order(4)
  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleRuntimeException(RuntimeException e) {
    logger.error("RuntimeException : ", e);
  }

  @Order(5)
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleException(Exception e) {
    logger.error("Exception : ", e);
  }
}
