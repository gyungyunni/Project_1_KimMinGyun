package com.example.mutsamarket.entity;

import com.example.mutsamarket.dto.replyDto.ReplyDto;
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
public class Comment extends BaseEntity {
    @Id   //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String writer;

    private String password;

    private String content;

    private String reply;

    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Reply> replies = new ArrayList<>(); //NullPointExeption 방지

    @ManyToOne
    private SalesItem salesItem;


}

