package com.example.mutsamarket.dto.negotiationDto;

import com.example.mutsamarket.entity.Negotiation;
import lombok.Data;

@Data
public class NegotiationReadDto {
    private Integer id;

    private int suggestedPrice;

    private String status;

    public static NegotiationReadDto fromEntity(Negotiation entity) {

        NegotiationReadDto dto = new NegotiationReadDto();
        dto.setId(Math.toIntExact(entity.getId()));
        dto.setSuggestedPrice(entity.getSuggestedPrice());
        dto.setStatus(entity.getStatus());

        return dto;
    }
}
