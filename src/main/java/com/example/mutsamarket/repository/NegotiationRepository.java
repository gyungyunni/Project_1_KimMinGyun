package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.Negotiation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NegotiationRepository extends JpaRepository<Negotiation, Integer> {

    Page<Negotiation> findAllBySalesItemId(Long itemId, Pageable pageable);

    Optional<Negotiation> findTopBySalesItemWriterAndSalesItemPassword(String writer, String password);

    Optional<Negotiation> findTopByWriterAndPassword(String writer, String password);

    Optional<Negotiation> findBySalesItemIdAndIdAndWriterAndPassword(Long salesItem_id, Integer id, String writer, String password);

    Optional<Negotiation> findBySalesItemIdAndIdAndSalesItemWriterAndSalesItemPassword(Long salesItem_id, Integer id, String writer, String password);

    Optional<Negotiation> findBySalesItemIdAndIdAndWriterAndPasswordAndStatus(Long salesItem_id,Integer id, String writer, String password, String status);

    List<Negotiation> findAllBySalesItemIdAndIdNot(Long salesItem_id, Integer id);
}
