package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.Negotiation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NegotiationRepository extends JpaRepository<Negotiation, Integer> {

    Page<Negotiation> findAllBySalesItemId(Long itemId, Pageable pageable);

    @Query("SELECT n FROM Negotiation n " +
            "JOIN n.salesItem si " +
            "JOIN n.user u " +
            "WHERE si.id = :itemId " +
            "AND u.username = :username")
    Page<Negotiation> findAllBySalesItemIdAndUsername(
            @Param("itemId") Long itemId,
            @Param("username") String username,
            Pageable pageable
    );

    @Query("SELECT n FROM Negotiation n " +
            "JOIN n.salesItem si " +
            "JOIN n.user u " +
            "WHERE n.id = :negotiationId " +
            "AND si.id = :itemId " +
            "AND u.username = :username")
    Optional<Negotiation> findByNegotiationIdAndSalesItemIdAndUsername(
            @Param("negotiationId") Long negotiationId,
            @Param("itemId") Long itemId,
            @Param("username") String username
    );

    @Query("SELECT n FROM Negotiation n " +
            "JOIN n.user u " +
            "WHERE u.username = :username ")
    Optional<Negotiation> findByUsername(@Param("username") String username);

}
