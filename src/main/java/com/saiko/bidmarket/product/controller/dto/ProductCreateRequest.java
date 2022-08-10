package com.saiko.bidmarket.product.controller.dto;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import com.saiko.bidmarket.product.Category;

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

  public ProductCreateRequest(String title, String description,
                              List<String> images, Category category, int minimumPrice,
                              String location) {
    this.title = title;
    this.description = description;
    this.images = images;
    this.category = category;
    this.minimumPrice = minimumPrice;
    this.location = location;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public List<String> getImages() {
    return images;
  }

  public Category getCategory() {
    return category;
  }

  public int getMinimumPrice() {
    return minimumPrice;
  }

  public String getLocation() {
    return location;
  }
}
