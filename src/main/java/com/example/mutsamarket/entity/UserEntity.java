package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;
    private String phone;
    private String address;

    // 토큰 발행후 들어간다.
    private String token; // 토큰

    @OneToMany
    @JoinColumn(name = "user_id") //중간 생기는 테이블 없애기 위해서
    private List<SalesItem> salesItems = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "user_id") //중간 생기는 테이블 없애기 위해서
    private List<SalesItem> comments = new ArrayList<>();
}