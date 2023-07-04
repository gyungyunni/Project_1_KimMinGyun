package com.example.mutsamarket.repository;

import com.example.mutsamarket.dto.replyDto.ReplyDto;
import com.example.mutsamarket.entity.Comment;
import com.example.mutsamarket.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

    Set<ReplyDto> findByCommentId(Long commentId);
}
