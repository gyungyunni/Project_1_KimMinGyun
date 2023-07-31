package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "users")
public class UserEntity extends BaseEntity {
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

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id") //중간 생기는 테이블 없애기 위해서
    private List<SalesItem> salesItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id") //중간 생기는 테이블 없애기 위해서
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id") //중간 생기는 테이블 없애기 위해서
    private List<Negotiation> negotiations = new ArrayList<>();

}