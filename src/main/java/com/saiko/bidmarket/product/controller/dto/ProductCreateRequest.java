package com.saiko.bidmarket.product.controller.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import com.saiko.bidmarket.product.Category;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ProductCreateRequest {
  @NotBlank
  @Length(max = 32)
  private final String title;

  @NotBlank
  @Length(max = 500)
  private final String description;

  @NotEmpty
  @Size(max = 5)
  private final List<String> images;

  @NotNull
  private final Category category;

  @Min(value = 1000)
  private final int minimumPrice;

  @Length(max = 20)
  private final String location;
}
