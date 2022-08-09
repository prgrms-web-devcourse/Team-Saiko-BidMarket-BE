package com.saiko.bidmarket.notification;

public enum NotificationType {
  END_PRODUCT_FOR_WRITER_WITH_WINNER("입찰 종료", "등록하신 상품이 판매되었습니다. 낙찰자분과 채팅을 시작해보세요!"),
  END_PRODUCT_FOR_WRITER_NOT_WITH_WINNER("입찰 종료", "아쉽게도 상품이 판매되지 않았어요. :( 상품을 재등록하시겠어요?"),
  END_PRODUCT_FOR_WINNER("입찰 종료", "축하드립니다! 낙찰에 성공하신 제품이 있네요!"),
  END_PRODUCT_FOR_BIDDER("입찰 종료", "아쉽게도 이번에는 낙찰 받지 못하셨어요. :( 다음에 다시 도전해주세요.");

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
