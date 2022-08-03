package com.saiko.bidmarket.product;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
  DIGITAL_DEVICE("디지털 기기"),
  HOUSEHOLD_APPLIANCE("생활 가전"),
  FURNITURE("가구/인테리어"),
  CHILDREN_BOOK("유아 도서"),
  FOOD("생활/가공 식품"),
  SPORTS_LEISURE("스포츠/레저"),
  WOMAN_GOODS("여성 잡화"),
  WOMAN_CLOTHES("여성 의류"),
  MAN_FASHION_GOODS("남성패션/잡화"),
  HOBBY("게임/취미"),
  BEAUTY("뷰티/미용"),
  PET_SUPPLY("반려 동물 용품"),
  BOOK_TICKET_RECORD("도서/티켓/음반"),
  PLANT("식물"),
  ETC("기타 중고 물품");

  private final String displayName;

  Category(String displayName) {
    this.displayName = displayName;
  }

  @JsonCreator
  public static Category from(String name) {
    for (Category category : Category.values()) {
      if (category.name().equals(name)) {
        return category;
      }
    }
    return null;
  }

  @JsonValue
  public String getDisplayName() {
    return displayName;
  }
}

