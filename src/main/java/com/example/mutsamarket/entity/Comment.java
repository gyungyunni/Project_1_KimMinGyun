package com.example.mutsamarket.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseEntity {
    @Id   //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;

    private String password;

    private String content;

    private String reply;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}) //comment를 작성, 삭제하면 salesItem 엔티티에서도 반영되게
    @ToString.Exclude
    private SalesItem salesItem;                                                       // 댓글은 판매글 하나에 여러개가 달릴 수 있음

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}) //comment를 작성, 삭제하면 user 엔티티에서도 반영되게
    @ToString.Exclude
    private UserEntity user;                                                           // 댓글은 한 유저가 여러번 달 수 있음

}

