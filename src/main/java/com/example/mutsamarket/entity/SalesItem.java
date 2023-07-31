package com.example.mutsamarket.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

@Entity
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name = "items")
public class SalesItem extends BaseEntity {
    @Id   //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String imageUrl;

    private int minPriceWanted;

    private String status;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) //1대N 관계
    @JoinColumn(name = "itemId") //itemId 속성이 CommentEntity에 생기게됨
    @ToString.Exclude
    private List<Comment> comments = new ArrayList<>(); //NullPointExeption 방지

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemId")
    @ToString.Exclude
    private List<Negotiation> negotiations = new ArrayList<>();

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //판매글을 작성, 삭제하면 user 엔티티에서도 반영되게
    @ToString.Exclude
    private UserEntity user;
}
