package com.example.mutsamarket.dto.salesItemDto;

import com.example.mutsamarket.entity.SalesItem;
import lombok.Data;

@Data
public class SalesItemReadDto {
    private String title;

    private String description;

    private int minPriceWanted;

    private String status;

    public static SalesItemReadDto fromEntity(SalesItem entity)
    {
        SalesItemReadDto dto = new SalesItemReadDto();
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getWriter());
        dto.setMinPriceWanted(entity.getMinPriceWanted());
        dto.setStatus(entity.getStatus());
        return dto;
    }

}
