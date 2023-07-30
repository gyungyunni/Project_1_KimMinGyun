package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.Comment;
import com.example.mutsamarket.entity.SalesItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAllBySalesItemId(Long itemId, Pageable pageable);

    Comment findBySalesItemIdAndId(Long itemId, Long id);

    @Query("SELECT c FROM Comment c " +
            "JOIN c.salesItem si " +
            "JOIN c.user u " +
            "WHERE c.id = :commentId " +
            "AND si.id = :itemId " +
            "AND u.username = :username")
    Optional<Comment> findByCommentIdAndSalesItemIdAndUsername(
            @Param("commentId") Long commentId,
            @Param("itemId") Long itemId,
            @Param("username") String username
    );

}
