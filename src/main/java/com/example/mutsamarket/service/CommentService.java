package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.commentDto.CommentEnrollDto;
import com.example.mutsamarket.dto.commentDto.CommentsReadDto;
import com.example.mutsamarket.dto.commentDto.ReplyDto;
import com.example.mutsamarket.entity.Comment;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.CommentRepository;
import com.example.mutsamarket.repository.SalesItemRepository;
import com.example.mutsamarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final SalesItemRepository salesItemRepository;
    private final UserRepository userRepository;

    //댓글 등록
    public void enrollComment(CommentEnrollDto dto, Long itemId, Authentication authentication) {

        String loginUser = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(loginUser);
        UserEntity user = optionalUser.get();

        Comment newComment = new Comment();
        newComment.setContent(dto.getContent());
        newComment.setUser(user);
        newComment.setSalesItem(salesItemRepository.findById(itemId));
        newComment = commentRepository.save(newComment);

        // userRepository.findAll().forEach(System.out::println);

    }

    //댓글 페이지 조회, 인증 x
    public Page<CommentsReadDto> readCommentsPage(Long page, Long itemId) {
        Pageable pageable = PageRequest.of(Math.toIntExact(page), 25);
        Page<Comment> commentsPage
                = commentRepository.findAllBySalesItemId(itemId, pageable);

        return commentsPage.map(CommentsReadDto::fromEntity);
    }

    //댓글 수정
    public void updateComment(
            Long itemId,
            Long id,
            CommentEnrollDto dto,
            Authentication authentication
    ) {

        String username = authentication.getName();

        Optional<Comment> optionalComment
                = commentRepository.findByCommentIdAndSalesItemIdAndUsername(id, itemId, username);

        if (optionalComment.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Comment updateComment = optionalComment.get();
        updateComment.setContent(dto.getContent());
        updateComment = commentRepository.save(updateComment);

    }

    // 답글 추가, 물품 게시글 작성자만 사용할 수 있음
    // return 1이면 새로운 답변 추가, 2이면 답변 수정
    public int addReply(
            Long itemId,
            Long commentId,
            ReplyDto dto,
            Authentication authentication
    ){
        Comment co = commentRepository.findBySalesItemIdAndId(itemId, commentId);

        String username = authentication.getName();
        if(co.getReply() == null) {
            if(salesItemRepository.findById(itemId).getUser().getUsername().equals(username)) {
                Optional<Comment> optionalComment
                        = commentRepository.findByCommentIdAndSalesItemId(commentId, itemId);

                Comment comment = optionalComment.get();

                comment.setReply(dto.getReply());
                comment = commentRepository.save(comment);

                return 1;
            }
        }
        if(salesItemRepository.findById(itemId).getUser().getUsername().equals(username)) {

            Comment comment = commentRepository.findBySalesItemIdAndId(itemId, commentId);
            comment.setReply(dto.getReply());
            comment = commentRepository.save(comment);
            return 2;
        }
        else return 0;
    }

    // 댓글 삭제
    public boolean deleteComment(Long itemId, Long id, Authentication authentication) {

        String username = authentication.getName();

        Optional<Comment> optionalComment
                = commentRepository.findByCommentIdAndSalesItemIdAndUsername(id, itemId, username);

        if (optionalComment.isPresent()) {
            commentRepository.delete(optionalComment.get());

            // userRepository.findAll().forEach(System.out::println); //삭제될때 user 엔티티에서도 잘 삭제되나 확인용
            return true;
        } else return false;

    }

}
