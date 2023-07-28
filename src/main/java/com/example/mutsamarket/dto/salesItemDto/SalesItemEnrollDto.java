package com.example.mutsamarket.dto.salesItemDto;

import com.example.mutsamarket.entity.SalesItem;
import com.example.mutsamarket.entity.UserEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesItemEnrollDto {

        private Long id;

        @NotNull
        private String title;

        @NotNull
        private String description;

        private String imageUrl;

        @NotNull
        @Min(value = 0)
        private int minPriceWanted;

        private String status;

        private String writer;

        private String password;

        public static SalesItemEnrollDto fromEntity(SalesItem entity)
        {
                SalesItemEnrollDto dto = new SalesItemEnrollDto();
                dto.setId(entity.getId());
                dto.setTitle(entity.getTitle());
                dto.setDescription(entity.getDescription());
                dto.setMinPriceWanted(entity.getMinPriceWanted());
                dto.setStatus(entity.getStatus());
                return dto;
        }
}
