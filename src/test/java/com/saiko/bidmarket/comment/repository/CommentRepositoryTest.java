package com.saiko.bidmarket.comment.repository;

import static com.saiko.bidmarket.common.Sort.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.common.config.QueryDslConfig;
import com.saiko.bidmarket.product.Category;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.Group;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.GroupRepository;
import com.saiko.bidmarket.user.repository.UserRepository;

@DataJpaTest()
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = QueryDslConfig.class)
public class CommentRepositoryTest {
  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProductRepository productRepository;

  @Nested
  @DisplayName("findAllByProduct 메소드는")
  class DescribeFindAllByProduct {

    @Nested
    @DisplayName("CommentSelectRequest 가 null 이라면")
    class ContextWithCommentSelectRequestNull {

      @Test
      @DisplayName("InvalidDataAccessApiUsageException 에러를 발생시킨다")
      void ItThrowsInvalidDataAccessApiUsageException() {
        //when, then
        assertThatThrownBy(() -> commentRepository.findAllByProduct(null))
            .isInstanceOf(InvalidDataAccessApiUsageException.class);
      }
    }

    @Nested
    @DisplayName("정렬 조건이 null 이라면")
    class ContextSortNull {

      @Test
      @DisplayName("등록순으로 정렬된 상품 목록 리스트를 반환한다")
      void itReturnCreatedAtAscProductList() {
        // given
        Group group = groupRepository
            .findById(1L)
            .get();

        User writer = User
            .builder()
            .username("제로")
            .profileImage("image")
            .provider("google")
            .providerId("123")
            .group(group)
            .build();
        writer = userRepository.save(writer);
        Product product = productRepository.save(Product
                                                     .builder()
                                                     .title("노트북 팝니다1")
                                                     .description("싸요")
                                                     .category(Category.DIGITAL_DEVICE)
                                                     .minimumPrice(10000)
                                                     .images(List.of("image"))
                                                     .location(null)
                                                     .writer(writer)
                                                     .build());

        User commentWriter = User
            .builder()
            .username("레이")
            .profileImage("image")
            .provider("google")
            .providerId("1234")
            .group(group)
            .build();

        commentWriter = userRepository.save(commentWriter);

        Comment comment = Comment
            .builder()
            .writer(commentWriter)
            .product(product)
            .content("안녕하세요~")
            .build();
        comment = commentRepository.save(comment);

        CommentSelectRequest commentSelectRequest = new CommentSelectRequest(
            product.getId(),
            CREATED_AT_ASC
        );

        // when
        List<Comment> result = commentRepository.findAllByProduct(commentSelectRequest);

        // then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(comment);
      }
    }
  }
}
