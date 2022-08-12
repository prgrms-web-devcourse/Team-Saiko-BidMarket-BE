package com.saiko.bidmarket.product.service;

import static com.saiko.bidmarket.product.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.bidding.entity.Bidding;
import com.saiko.bidmarket.bidding.repository.BiddingRepository;
import com.saiko.bidmarket.chat.entity.ChatRoom;
import com.saiko.bidmarket.chat.repository.ChatRoomRepository;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.Role;
import com.saiko.bidmarket.product.controller.dto.BiddingResultResponse;
import com.saiko.bidmarket.product.controller.dto.ProductCreateRequest;
import com.saiko.bidmarket.product.controller.dto.ProductCreateResponse;
import com.saiko.bidmarket.product.controller.dto.ProductDetailResponse;
import com.saiko.bidmarket.product.controller.dto.ProductSelectRequest;
import com.saiko.bidmarket.product.controller.dto.ProductSelectResponse;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultProductServiceTest {

  @Mock
  ProductRepository productRepository;

  @Mock
  ChatRoomRepository chatRoomRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  BiddingRepository biddingRepository;

  @Mock
  ApplicationEventPublisher publisher;

  @InjectMocks
  DefaultProductService productService;

  private static final ProductCreateRequest productCreateRequest = new ProductCreateRequest(
      "텀블러 팝니다", "깨끗해요", List.of("image1", "image2"), ETC, 15000, "강남");
  private static final ProductSelectRequest productSelectRequest = new ProductSelectRequest(
      null,
      "true",
      null,
      0,
      2,
      null
  );
  private static final User writer = User
      .builder()
      .username("레이")
      .profileImage("image")
      .provider("google")
      .providerId("123")
      .group(new Group())
      .build();
  private static final Product product = Product
      .builder()
      .title("책 팔아요")
      .writer(writer)
      .description("깨끗해요")
      .images(List.of("image"))
      .category(CHILDREN_BOOK)
      .minimumPrice(10000)
      .location("직거래 안해요")
      .build();
  private static final User successfulBidder = User
      .builder()
      .username("제로")
      .profileImage("image")
      .provider("google")
      .providerId("123")
      .group(new Group())
      .build();
  private static final User failedBidder = User
      .builder()
      .username("레이")
      .profileImage("image")
      .provider("google")
      .providerId("1234")
      .group(new Group())
      .build();

  private static final Bidding successfulBidding = Bidding
      .builder()
      .bidder(successfulBidder)
      .product(product)
      .biddingPrice(10100)
      .build();
  private static final Bidding failedBidding = Bidding
      .builder()
      .bidder(failedBidder)
      .product(product)
      .biddingPrice(10000)
      .build();
  private static final ChatRoom chatRoom = ChatRoom
      .builder()
      .winner(successfulBidder)
      .seller(writer)
      .product(product)
      .build();
  private static final long writerId = 1;
  private static final long productId = 1;
  private static final long successfulBidderId = 2;
  private static final long failedBidderId = 3;
  private static final long successfulBiddingId = 1;
  private static final long failedBiddingId = 2;
  private static final long chatRoomId = 1;

  @BeforeAll
  static void setupDomain() {
    ReflectionTestUtils.setField(writer, "id", writerId);
    ReflectionTestUtils.setField(product, "id", productId);
    ReflectionTestUtils.setField(successfulBidder, "id", successfulBidderId);
    ReflectionTestUtils.setField(failedBidder, "id", failedBidderId);
    ReflectionTestUtils.setField(successfulBidding, "id", successfulBiddingId);
    ReflectionTestUtils.setField(failedBidding, "id", failedBiddingId);
    ReflectionTestUtils.setField(chatRoom, "id", chatRoomId);
    product.finish();
  }

  @Nested
  @DisplayName("create 메서드는")
  class DescribeCreate {

    @Nested
    @DisplayName("Request 값이 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> productService.create(null, 1L))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("user id 값이 양수가 아니면")
    class ContextWithNotPositiveUserId {

      @ParameterizedTest
      @ValueSource(longs = {0, -1, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException(long userId) {
        //given
        //when, then
        assertThatThrownBy(() -> productService.create(productCreateRequest, userId))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArg {

      @Test
      @DisplayName("저장한 객체에 대한 정보를 담은 응답을 반환한다")
      void ItResponseProduct() {
        //given
        given(userRepository.findById(writerId)).willReturn(Optional.of(writer));
        given(productRepository.save(any())).willReturn(product);

        //when
        ProductCreateResponse response = productService.create(productCreateRequest, writerId);

        //then
        verify(productRepository).save(any(Product.class));
        assertThat(response.getId()).isEqualTo(product.getId());
      }
    }
  }

  @Nested
  @DisplayName("findAll 메서드는")
  class DescribeFindAll {

    @Nested
    @DisplayName("Request 가 null 이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> productService.findAll(null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArgument {

      @Test
      @DisplayName("요청에 해당하는 상품 리스트를 반환한다")
      void ItResponseProductList() {
        //given
        given(productRepository.findAllProduct(any(ProductSelectRequest.class)))
            .willReturn(List.of(product));

        //when
        List<ProductSelectResponse> result = productService.findAll(productSelectRequest);

        //then
        verify(productRepository).findAllProduct(any(ProductSelectRequest.class));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result
                       .get(0)
                       .getId()).isEqualTo(product.getId());
      }
    }
  }

  @Nested
  @DisplayName("findById 메서드는")
  class DescribeFindById {

    @Nested
    @DisplayName("id에 해당하는 상품이 없다면")
    class ContextNotFoundProductById {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        long inputId = Long.MAX_VALUE;

        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> productService.findById(inputId))
            .isInstanceOf(NotFoundException.class);
        verify(productRepository, atLeastOnce()).findById(anyLong());
      }
    }

    @Nested
    @DisplayName("id 값이 양수가 아니면")
    class ContextWith {

      @ParameterizedTest
      @ValueSource(longs = {0, -1L, Long.MIN_VALUE})
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException(long src) {
        //when, then
        assertThatThrownBy(() -> productService.findById(src))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("양수값이 전달되면")
    class ContextWithPositiveId {

      @Test
      @DisplayName("찾은 객체에 대한 응답을 반환한다")
      void ItProduct() {
        //given
        User writer = new User("제로", "image", "google", "1234", new Group());
        Product product = Product
            .builder()
            .title("세탁기 팔아요")
            .description("좋아요")
            .minimumPrice(100000)
            .category(HOUSEHOLD_APPLIANCE)
            .location("수원")
            .writer(writer)
            .images(null)
            .build();
        ReflectionTestUtils.setField(writer, "id", 1L);
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

        //when
        ProductDetailResponse response = productService.findById(1L);

        //then
        assertThat(response.getId()).isEqualTo(product.getId());
      }
    }
  }

  @Nested
  @DisplayName("findAllThatNeedToClose 메서드는")
  class DescribeFindAllThatNeedToClose {

    @Nested
    @DisplayName("nowTime이 null 이면")
    class ContextWithNullStart {

      @Test
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> productService.findAllThatNeedToClose(null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArgument {

      @Test
      @DisplayName("요청에 해당하는 상품 리스트를 반환한다")
      void ItResponseProductList() {
        //when, then
        User writer = new User("제로", "image", "google", "1234", new Group());
        Product product = Product
            .builder()
            .title("세탁기 팔아요")
            .description("좋아요")
            .minimumPrice(100000)
            .category(HOUSEHOLD_APPLIANCE)
            .location("수원")
            .writer(writer)
            .images(null)
            .build();
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productRepository.findAllByProgressedAndExpireAtLessThan(
            anyBoolean(), any(LocalDateTime.class))).willReturn(List.of(product));

        //when
        List<Product> result = productService.findAllThatNeedToClose(LocalDateTime.now());

        //then
        verify(productRepository).findAllByProgressedAndExpireAtLessThan(anyBoolean(), any());
        assertThat(result.size()).isEqualTo(1);
        assertThat(result
                       .get(0)
                       .getId()).isEqualTo(product.getId());
      }
    }
  }

  @Nested
  @DisplayName("executeClosingProduct 메서드는")
  class DescribeExecuteClosingProduct {

    @Nested
    @DisplayName("product 가 null 값이 전달되면")
    class ContextWithEmptyProduct {

      @Test
      @DisplayName("IllegalArgumentException 예외를 던진다")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> productService.executeClosingProduct(null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArgument {

      @Test
      @DisplayName("알림 생성 이벤트를 발생시킨다.")
      void ItGenerateCreatingNotificationEvent() {
        //given
        User writer = User
            .builder()
            .username("writer")
            .profileImage("imageURL")
            .provider("provider")
            .providerId("providerId")
            .group(new Group())
            .build();
        User bidderOne = User
            .builder()
            .username("bidderOne")
            .profileImage("imageURL")
            .provider("provider")
            .providerId("providerId")
            .group(new Group())
            .build();
        Product product = Product
            .builder()
            .title("title")
            .description("description")
            .minimumPrice(10000)
            .writer(writer)
            .category(Category.ETC)
            .build();
        Bidding biddingOne = Bidding
            .builder()
            .biddingPrice(20000L)
            .bidder(bidderOne)
            .product(product)
            .build();
        Bidding biddingTwo = Bidding
            .builder()
            .biddingPrice(30000L)
            .bidder(bidderOne)
            .product(product)
            .build();
        ReflectionTestUtils.setField(product, "id", 1L);
        ReflectionTestUtils.setField(product, "biddings", List.of(biddingTwo, biddingOne));

        //when
        productService.executeClosingProduct(product);

        //then
        verify(publisher, times(3)).publishEvent(any(Object.class));
      }
    }
  }

  @Nested
  @DisplayName("getBiddingResult 메서드는")
  class DescribeGetBiddingResult {

    @Nested
    @DisplayName("id에 해당하는 상품이 없다면")
    class ContextNotFoundProductById {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        given(productRepository.findByIdJoinWithUser(1))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(
            () -> productService.getBiddingResult(1, 1))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("채팅방을 찾을 수 없다면")
    class ContextNotFoundChatRoom {

      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        given(productRepository.findByIdJoinWithUser(anyLong()))
            .willReturn(Optional.of(product));
        given(chatRoomRepository.findByProduct_IdAndSeller_Id(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> productService.getBiddingResult(product.getId(), writer.getId()))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("판매자이고 낙찰에 성공한다면")
    class ContextSuccessfulSeller {

      @Test
      @DisplayName("비딩 결과를 반환한다")
      void ItReturnBiddingResult() {
        // given
        given(productRepository.findByIdJoinWithUser(anyLong()))
            .willReturn(Optional.of(product));
        given(chatRoomRepository.findByProduct_IdAndSeller_Id(anyLong(), anyLong()))
            .willReturn(Optional.of(chatRoom));

        // when
        BiddingResultResponse biddingResult = productService.getBiddingResult(
            product.getId(),
            writer.getId()
        );

        // then
        verify(productRepository).findByIdJoinWithUser(anyLong());
        verify(chatRoomRepository).findByProduct_IdAndSeller_Id(anyLong(), anyLong());
        assertThat(biddingResult.isBiddingSucceed()).isEqualTo(true);
        assertThat(biddingResult
                       .getChatRoomId()).isEqualTo(chatRoom.getId());
        assertThat(biddingResult.getRole()).isEqualTo(Role.SELLER);
      }
    }

    @Nested
    @DisplayName("판매자이고 상품이 낙찰에 실패한다면")
    class ContextFailedSeller {

      @Test
      @DisplayName("비딩 결과를 반환한다")
      void ItReturnBiddingResult() {
        // given
        User writer = User
            .builder()
            .username("레이")
            .profileImage("image")
            .provider("google")
            .providerId("123")
            .group(new Group())
            .build();
        ReflectionTestUtils.setField(writer, "id", 1L);

        Product product = Product
            .builder()
            .title("책 팔아요")
            .writer(writer)
            .description("깨끗해요")
            .images(List.of("image"))
            .category(CHILDREN_BOOK)
            .minimumPrice(10000)
            .location("직거래 안해요")
            .build();
        ReflectionTestUtils.setField(product, "id", 1L);

        given(productRepository.findByIdJoinWithUser(anyLong()))
            .willReturn(Optional.of(product));
        given(chatRoomRepository.findByProduct_IdAndSeller_Id(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        // when
        BiddingResultResponse biddingResult = productService.getBiddingResult(
            product.getId(),
            writer.getId()
        );

        // then
        assertThat(biddingResult.isBiddingSucceed()).isEqualTo(false);
        assertThat(biddingResult.getChatRoomId()).isEqualTo(null);
        assertThat(biddingResult.getRole()).isEqualTo(Role.SELLER);
      }
    }

    @Nested
    @DisplayName("입찰자이고 낙찰에 성공한다면")
    class ContextSuccessfulBidder {

      @Test
      @DisplayName("비딩 결과를 반환한다")
      void ItReturnBiddingResult() {
        // given
        given(productRepository.findByIdJoinWithUser(anyLong()))
            .willReturn(Optional.of(product));
        given(chatRoomRepository.findByProduct_IdAndSeller_Id(anyLong(), anyLong()))
            .willReturn(Optional.of(chatRoom));
        given(biddingRepository.findByBidderIdAndProductId(anyLong(), anyLong()))
            .willReturn(Optional.of(successfulBidding));

        // when
        BiddingResultResponse biddingResult = productService.getBiddingResult(
            product.getId(),
            successfulBidder.getId()
        );

        // then
        verify(productRepository).findByIdJoinWithUser(anyLong());
        verify(chatRoomRepository).findByProduct_IdAndSeller_Id(anyLong(), anyLong());
        verify(biddingRepository).findByBidderIdAndProductId(anyLong(), anyLong());
        assertThat(biddingResult.isBiddingSucceed()).isEqualTo(true);
        assertThat(biddingResult.getChatRoomId()).isEqualTo(chatRoom.getId());
        assertThat(biddingResult.getRole()).isEqualTo(Role.BIDDER);
      }
    }

    @Nested
    @DisplayName("입찰자이고 낙찰에 실패한다면")
    class ContextFailedBidder {

      @Test
      @DisplayName("비딩 결과를 반환한다")
      void ItReturnBiddingResult() {
        // given
        given(productRepository.findByIdJoinWithUser(anyLong()))
            .willReturn(Optional.of(product));
        given(chatRoomRepository.findByProduct_IdAndSeller_Id(anyLong(), anyLong()))
            .willReturn(Optional.of(chatRoom));
        given(biddingRepository.findByBidderIdAndProductId(anyLong(), anyLong()))
            .willReturn(Optional.of(failedBidding));

        // when
        BiddingResultResponse biddingResult = productService.getBiddingResult(
            product.getId(),
            failedBidder.getId()
        );

        // then
        assertThat(biddingResult.isBiddingSucceed()).isEqualTo(false);
        assertThat(biddingResult.getChatRoomId()).isEqualTo(null);
        assertThat(biddingResult.getRole()).isEqualTo(Role.BIDDER);
      }
    }

    @Nested
    @DisplayName("입찰 한 적 없는 사용자라면")
    class ContextNotBidder {
      @Test
      @DisplayName("NotFoundException을 발생시킨다.")
      void ItThrowsNotFoundException() {
        // given
        given(productRepository.findByIdJoinWithUser(anyLong()))
            .willReturn(Optional.of(product));
        given(chatRoomRepository.findByProduct_IdAndSeller_Id(anyLong(), anyLong()))
            .willReturn(Optional.empty());
        given(biddingRepository.findByBidderIdAndProductId(anyLong(), anyLong()))
            .willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> productService.getBiddingResult(product.getId(), 2))
            .isInstanceOf(NotFoundException.class);
      }
    }
  }
}
