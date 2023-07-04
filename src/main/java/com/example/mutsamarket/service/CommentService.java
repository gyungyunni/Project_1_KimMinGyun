package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.commentDto.CommentEnrollDto;
import com.example.mutsamarket.dto.commentDto.CommentsReadDto;
import com.example.mutsamarket.dto.replyDto.ReplyDto;
import com.example.mutsamarket.entity.Comment;
import com.example.mutsamarket.entity.Reply;
import com.example.mutsamarket.entity.SalesItem;
import com.example.mutsamarket.repository.CommentRepository;
import com.example.mutsamarket.repository.ReplyRepository;
import com.example.mutsamarket.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final SalesItemRepository salesItemRepository;
    private final ReplyRepository replyRepository;

    public void enrollComment(CommentEnrollDto dto, Long itemId) {

        Comment newComment = new Comment();
        newComment.setWriter(dto.getWriter());
        newComment.setPassword(dto.getPassword());
        newComment.setContent(dto.getContent());
        newComment.setSalesItem(salesItemRepository.findById(itemId));
        newComment = commentRepository.save(newComment);

    }

    public Page<CommentsReadDto> readCommentsPage(Long page, Long itemId) {
        Pageable pageable = PageRequest.of(Math.toIntExact(page), 25);
        Page<Comment> commentsPage
                = commentRepository.findAllBySalesItemId(itemId, pageable);

        return commentsPage.map(CommentsReadDto::fromEntity);
    }

    public void updateComment(
            Long itemId,
            Long id,
            CommentEnrollDto dto
    ) {
        // 아니면 로직 진행
        Optional<Comment> optionalComment = commentRepository.findBySalesItemIdAndIdAndWriterAndPassword(itemId, id,  dto.getWriter(), dto.getPassword());

        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Comment updateComment = optionalComment.get();
        updateComment.setContent(dto.getContent());
        updateComment = commentRepository.save(updateComment);

    }

    //reply를 받고 replyRepository에 저장했지만, commet를 read할때 업뎃까지는 구현하지 못함
    public int addReply(
            Long itemId,
            Long commentId,
            ReplyDto dto
    ){
        Comment co = commentRepository.findBySalesItemIdAndId(itemId, commentId);
        if(co.getReply() == null) {
            Reply newReply = new Reply();
            newReply.setWriter(dto.getWriter());
            newReply.setReply(dto.getReply());
            newReply.setPassword(dto.getPassword());
            newReply.setComment(commentRepository.findBySalesItemIdAndId(itemId, commentId));
            newReply = replyRepository.save(newReply);

            Comment comment = commentRepository.findBySalesItemIdAndId(itemId, commentId);
            comment.setReply(dto.getReply());
            comment = commentRepository.save(comment);

            return 1;
        }
        if(salesItemRepository.findById(itemId).getWriter().equals(dto.getWriter())) {
            Reply newReply = new Reply();
            newReply.setWriter(dto.getWriter());
            newReply.setReply(dto.getReply());
            newReply.setPassword(dto.getPassword());
            newReply.setComment(commentRepository.findBySalesItemIdAndId(itemId, commentId));
            newReply = replyRepository.save(newReply);

            Comment comment = commentRepository.findBySalesItemIdAndId(itemId, commentId);
            comment.setReply(dto.getReply());
            comment = commentRepository.save(comment);
            return 2;
        }
        else return 0;
    }

    public boolean deleteComment(Long itemId, Long id, String writer, String password) {
        Optional<Comment> optionalComment
                = commentRepository.findBySalesItemIdAndIdAndWriterAndPassword(itemId,id, writer, password);

        if (optionalComment.isPresent()) {
            // DTO로 전환 후 반환
            commentRepository.delete(optionalComment.get());
            // 아니면 404
            return true;
        } else return false;

    }

}