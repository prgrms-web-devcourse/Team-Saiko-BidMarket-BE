package com.saiko.bidmarket.common.util;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
class IdEncoderTest {

  private final IdEncoder idEncoder = new IdEncoder();
  private final Logger log = LoggerFactory.getLogger(IdEncoderTest.class);

  @Nested
  @Order(1)
  @DisplayName("encode 메서드는")
  class DescribeEncode {

    @Nested
    @DisplayName("양수 값이 전달되면")
    class ContextWithPositiveValue {

      @ParameterizedTest
      @ValueSource(longs = {1, Long.MAX_VALUE})
      @DisplayName("타입을 명시하고 해시 값을 반환한다")
      void ItReturnHashValue(long src) {

        //when
        final var encodedId = idEncoder.encode(src);
        log.info(encodedId);
      }
    }

    @Nested
    @DisplayName("양수가 아닌 값이 전달되면")
    class ContextWithNotPositive {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 에러를 반환한다")
      void ItThrowsIllegalArgumentException(long src) {
        assertThrows(IllegalArgumentException.class,
                     () -> idEncoder.encode(src));
      }
    }
  }

  @Nested
  @Order(2)
  @DisplayName("decode 메서드는")
  class DescribeDecode {

    @Nested
    @DisplayName("인코딩된 값이 전달되면")
    class ContextWithHashValue {

      @Test
      @DisplayName("디코딩된 값을 반환한다")
      void ItReturnsDecodedValue() {
        final var testId = 12L;
        final var encoded = idEncoder.encode(testId);

        final var decoded = idEncoder.decode(encoded);
        assertEquals(testId, decoded);
      }
    }

    @Nested
    @DisplayName("유효하지 않은 해시값이 전달되면")
    class ContextWithNullOrEmpty {

      @ParameterizedTest
      @NullAndEmptySource
      @ValueSource(strings = {"\n", "\t", "", " ", "aa", "1a2s"})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다")
      void ItThrowsIllegalArgumentException(String src) {
        assertThrows(IllegalArgumentException.class, () -> idEncoder.decode(src));
      }
    }
  }
}
