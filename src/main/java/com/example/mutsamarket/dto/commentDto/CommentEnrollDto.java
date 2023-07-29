package com.example.mutsamarket.dto.commentDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CommentEnrollDto {

    private Long id;

    @NotNull
    private String content;

    private Integer itemId;

}
