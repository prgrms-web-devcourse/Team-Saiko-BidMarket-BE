package com.saiko.bidmarket.notification;

public enum NotificationType {
  END_PRODUCT_FOR_WRITER("입찰 종료", "상품의 입찰 기간이 종료되었습니다. 등록하신 게시글을 확인해주세요!"),
  END_PRODUCT_FOR_WINNER("입찰 종료", "상품의 입찰 기간이 종료되었습니다. 게시글을 확인하고 거래 일정을 잡아보세요!"),
  END_PRODUCT_FOR_BIDDER("입찰 종료", "상품의 입찰 기간이 종료되었습니다. 게시글을 확인해주세요!");

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
