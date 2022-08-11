package com.saiko.bidmarket.comment.service;

import static com.saiko.bidmarket.product.Category.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectResponse;
import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.comment.repository.CommentRepository;
import com.saiko.bidmarket.common.Sort;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultCommentServiceTest {

  @Mock
  ProductRepository productRepository;

  @Mock
  UserRepository userRepository;

  @Mock
  CommentRepository commentRepository;

  @InjectMocks
  DefaultCommentService commentService;

  private static long productId = 1;
  private static long userId = 1L;
  private static long commentId = 1;

  private static CommentCreateRequest commentCreateRequest = new CommentCreateRequest(
      productId,
      "풍경 그림도 돼요?"
  );
  private static User writer = User
      .builder()
      .username("레이")
      .profileImage("image")
      .provider("google")
      .providerId("1234")
      .group(new Group())
      .build();
  private static Product product = Product
      .builder()
      .title("그림 그려드려요")
      .description("잘 그려요")
      .location("전주")
      .category(HOBBY)
      .minimumPrice(1000)
      .images(Collections.emptyList())
      .writer(writer)
      .build();
  private static Comment comment = Comment
      .builder()
      .writer(writer)
      .product(product)
      .content(commentCreateRequest.getContent())
      .build();

  @BeforeAll
  static void setup() {
    ReflectionTestUtils.setField(writer, "id", userId);
    ReflectionTestUtils.setField(product, "id", productId);
    ReflectionTestUtils.setField(comment, "id", commentId);
  }

  @Nested
  @DisplayName("create 메서드는")
  class DescribeCreate {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArg {

      @Test
      @DisplayName("댓글을 저장하고 댓글의 아이디를 반환한다")
      void ItResponseProduct() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.of(writer));
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        //when
        CommentCreateResponse response = commentService.create(
            userId, commentCreateRequest);

        //then
        verify(productRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
        verify(commentRepository).save(any(Comment.class));
        assertThat(response.getId()).isEqualTo(comment.getId());
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
        assertThatThrownBy(() -> commentService.create(userId, commentCreateRequest))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("Request 값이 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> commentService.create(userId, null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("유저를 찾을 수 없다면")
    class ContextWithNonexistentUser {

      @Test
      @DisplayName("NotFound 에러를 발생시킨다.")
      void ItThrowsNotFoundException() {
        //given
        given(userRepository.findById(anyLong()))
            .willThrow(NotFoundException.class);

        //when, then
        assertThatThrownBy(
            () -> commentService.create(userId, commentCreateRequest))
            .isInstanceOf(NotFoundException.class);
      }
    }

    @Nested
    @DisplayName("상품을 찾을 수 없다면")
    class ContextWithNonexistentProduct {

      @Test
      @DisplayName("NotFound 에러를 발생시킨다.")
      void ItThrowsNotFoundException() {
        //given
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(writer));
        given(productRepository.findById(anyLong()))
            .willThrow(NotFoundException.class);

        //when, then
        assertThatThrownBy(
            () -> commentService.create(userId, commentCreateRequest))
            .isInstanceOf(NotFoundException.class);
      }
    }
  }

  @Nested
  @DisplayName("findAllByProduct 메서드는")
  class DescribeFindAllByProduct {

    @Nested
    @DisplayName("유효한 값이 전달되면")
    class ContextWithValidArg {

      @Test
      @DisplayName("상품의 전체 댓글을 조회하고 반환한다")
      void ItResponseComment() {
        //given
        Product product = Product
            .builder()
            .title("그림 그려드려요")
            .description("잘 그려요")
            .location("전주")
            .category(HOBBY)
            .minimumPrice(1000)
            .images(Collections.emptyList())
            .writer(new User("제로", "image", "google", "1234", new Group()))
            .build();

        User commentWriter = new User("레이", "image", "google", "1234", new Group());
        ReflectionTestUtils.setField(commentWriter, "id", 1L);

        Comment comment = Comment
            .builder()
            .writer(commentWriter)
            .product(product)
            .content("연예인도 되나요?")
            .build();
        long commentId = 1l;
        ReflectionTestUtils.setField(comment, "id", commentId);

        CommentSelectRequest commentSelectRequest = new CommentSelectRequest(
            1,
            Sort.CREATED_AT_ASC
        );

        given(commentRepository.findAllByProduct(any(CommentSelectRequest.class)))
            .willReturn(List.of(comment));

        //when
        List<CommentSelectResponse> result = commentService.findAllByProduct(
            commentSelectRequest);

        //then
        verify(commentRepository).findAllByProduct(any(CommentSelectRequest.class));
        assertThat(result.size()).isEqualTo(1);
        assertThat(result
                       .get(0)
                       .getUserId()
                       .getValue()).isEqualTo(commentWriter.getId());
        assertThat(result
                       .get(0)
                       .getUsername()).isEqualTo(commentWriter.getUsername());
        assertThat(result
                       .get(0)
                       .getProfileImage()).isEqualTo(commentWriter.getProfileImage());
        assertThat(result
                       .get(0)
                       .getContent()).isEqualTo(comment.getContent());
        assertThat(result
                       .get(0)
                       .getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(result
                       .get(0)
                       .getUpdatedAt()).isEqualTo(comment.getUpdatedAt());
      }
    }

    @Nested
    @DisplayName("Request 값이 null이면")
    class ContextWithNullRequest {

      @Test
      @DisplayName("IllegalArgumentException 에러를 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        //when, then
        assertThatThrownBy(() -> commentService.findAllByProduct(null))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("댓글이 한건도 없다면")
    class ContextWithEmptyCommentList {

      @Test
      @DisplayName("빈 리스트를 반환한다")
      void ItReturnEmptyList() {
        //given
        CommentSelectRequest commentSelectRequest = new CommentSelectRequest(
            1l,
            Sort.CREATED_AT_ASC
        );
        given(commentRepository.findAllByProduct(any(CommentSelectRequest.class)))
            .willReturn(Collections.EMPTY_LIST);

        //when
        List<CommentSelectResponse> result = commentService.findAllByProduct(
            commentSelectRequest);

        //then
        assertThat(result.size()).isEqualTo(0);
      }
    }
  }
}
