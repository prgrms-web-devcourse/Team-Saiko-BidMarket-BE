package com.saiko.bidmarket.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saiko.bidmarket.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
