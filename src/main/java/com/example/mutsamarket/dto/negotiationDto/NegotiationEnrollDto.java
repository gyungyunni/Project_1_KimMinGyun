package com.example.mutsamarket.dto.negotiationDto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NegotiationEnrollDto {


    private int suggestedPrice;

    @NotNull
    private String writer;

    @NotNull
    private String password;

    private  String status;

}
