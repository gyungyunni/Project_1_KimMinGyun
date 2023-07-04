package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.Comment;
import com.example.mutsamarket.entity.SalesItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllBySalesItemId(Long itemId, Pageable pageable);

    Optional<Comment> findBySalesItemIdAndIdAndWriterAndPassword(Long itemId, Long id, String writer, String password);


    Comment findBySalesItemIdAndId(Long itemId, Long id);



}
