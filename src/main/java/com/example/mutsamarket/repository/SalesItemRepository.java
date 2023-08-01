package com.example.mutsamarket.repository;

import com.example.mutsamarket.entity.Negotiation;
import com.example.mutsamarket.entity.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SalesItemRepository extends JpaRepository<SalesItem, Integer> {

    // Optional<SalesItem> findByIdAndWriterAndPassword(Long id, String writer, String password);
    SalesItem findById(Long Id);

    // Optional<SalesItem> findByWriterAndPasswordAndId(String writer,String password,Long id);

    @Query(value = "SELECT si FROM SalesItem si JOIN si.user u WHERE si.id = :itemId AND u.username = :username")
    Optional<SalesItem> findByItemIdAndUsername(@Param("itemId") Long itemId, @Param("username") String username);

}
