package com.example.mutsamarket.dto.commentDto;

import com.example.mutsamarket.dto.replyDto.ReplyDto;
import com.example.mutsamarket.entity.Comment;

import com.example.mutsamarket.entity.Reply;
import lombok.Data;

@Data
public class CommentsReadDto {

    private Long id;

    private String content;

    private String reply;

    public static CommentsReadDto fromEntity(Comment entity)
    {
        CommentsReadDto dto = new CommentsReadDto();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setReply(entity.getReply());

        return dto;
    }

}
