package com.saiko.bidmarket.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.saiko.bidmarket.comment.controller.dto.CommentCreateRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentCreateResponse;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectRequest;
import com.saiko.bidmarket.comment.controller.dto.CommentSelectResponse;
import com.saiko.bidmarket.comment.entity.Comment;
import com.saiko.bidmarket.comment.repository.CommentRepository;
import com.saiko.bidmarket.common.entity.UnsignedLong;
import com.saiko.bidmarket.common.exception.NotFoundException;
import com.saiko.bidmarket.product.entity.Product;
import com.saiko.bidmarket.product.repository.ProductRepository;
import com.saiko.bidmarket.user.entity.User;
import com.saiko.bidmarket.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class DefaultCommentService implements CommentService {
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  @Override
  @Transactional
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

  @Override
  public List<CommentSelectResponse> findAllByProduct(CommentSelectRequest request) {
    Assert.notNull(request, "Request must be provided");

    return commentRepository.findAllByProduct(request)
                            .stream()
                            .map((comment -> CommentSelectResponse.from(comment)))
                            .collect(Collectors.toList());
  }
}
