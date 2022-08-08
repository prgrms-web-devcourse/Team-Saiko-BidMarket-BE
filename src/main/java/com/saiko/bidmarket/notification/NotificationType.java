package com.saiko.bidmarket.notification;

public enum NotificationType {
  END_PRODUCT_FOR_WRITER_WITH_WINNER("입찰 종료", "상품의 입찰 기간이 종료되었습니다. 낙찰자와 거래 일정을 잡아보세요!"),
  END_PRODUCT_FOR_WRITER_NOT_WITH_WINNER("입찰 종료", "상품의 입찰 기간이 종료되었습니다. 재등록하시겠습니까?"),
  END_PRODUCT_FOR_WINNER("입찰 종료", "상품의 입찰 기간이 종료되었습니다. 낙찰을 축하드립니다. 판매자와 거래 일정을 잡아보세요!"),
  END_PRODUCT_FOR_BIDDER("입찰 종료", "상품의 입찰 기간이 종료되었습니다. 아쉽지만 낙찰되지 않으셨습니다.");

  private final String type;
  private final String message;

  NotificationType(String type, String message) {
    this.type = type;
    this.message = message;
  }

  public String getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }
}
