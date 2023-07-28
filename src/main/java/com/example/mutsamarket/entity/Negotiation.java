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
@Table(name = "negotiation")
public class Negotiation extends BaseEntity  {
    @Id   //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int suggestedPrice;

    private String status;

    private String writer;

    private String password;

    @ManyToOne
    private SalesItem salesItem;

}

