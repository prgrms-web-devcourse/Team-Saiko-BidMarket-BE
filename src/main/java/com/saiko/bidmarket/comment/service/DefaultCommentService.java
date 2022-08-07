package com.saiko.bidmarket.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.comment.repository.CommentRepository;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

@Service
public class DefaultCommentService implements CommentService {
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  public DefaultCommentService(
      CommentRepository commentRepository,
      UserRepository userRepository,
      ProductRepository productRepository) {
    this.commentRepository = commentRepository;
    this.userRepository = userRepository;
    this.productRepository = productRepository;
  }

  @Override
  public CommentCreateResponse create(UnsignedLong userId, CommentCreateRequest request) {
    Assert.notNull(userId, "UserId must be provided");
    Assert.notNull(request, "Request must be provided");

    User writer = userRepository.findById(userId.getValue())
                                .orElseThrow(() -> new NotFoundException(
                                    "User does not exist"));
    Product product = productRepository.findById(request.getProductId().getValue())
                                       .orElseThrow(
                                           () -> new NotFoundException("Product does not exist"));

    Comment comment = Comment.builder()
                             .writer(writer)
                             .product(product)
                             .content(request.getContent())
                             .build();

    Comment savedComment = commentRepository.save(comment);
    return new CommentCreateResponse(UnsignedLong.valueOf(savedComment.getId()));
  }
}
