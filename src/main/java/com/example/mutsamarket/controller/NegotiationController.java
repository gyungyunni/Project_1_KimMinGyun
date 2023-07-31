package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.negotiationDto.NegotiationEnrollDto;
import com.example.mutsamarket.dto.negotiationDto.NegotiationReadDto;
import com.example.mutsamarket.service.NegotiationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/api/mutsamarket")
@RestController
@Slf4j
@RequiredArgsConstructor
public class NegotiationController {
    private final NegotiationService negotiationService;

    @PostMapping("/items/{itemId}/proposal")
    public ResponseEntity<Map<String, String>> enrollNegotiation(
            @Valid @RequestBody NegotiationEnrollDto dto,
            @PathVariable("itemId") Long itemId
    ) {
        negotiationService.enrollNegotiation(dto, itemId);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "구매 제안이 등록되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/items/{itemId}/proposals")
    public Page<NegotiationReadDto> readNegoPage(
            @RequestParam(value = "writer") String writer,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @PathVariable("itemId") Long itemId
    ){
        return negotiationService.readNegoPage(page, itemId,writer,password);
    }

    @PutMapping("/items/{itemId}/proposals/{proposalId}")
    public ResponseEntity<Map<String, String>> updateNegotiation(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long Id,
            @Valid @RequestBody NegotiationEnrollDto dto
    ) {
        if(negotiationService.updateNegotiation(itemId, Id, dto) == 1) {

            log.info(dto.toString());
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "제안이 수정되었습니다.");
            return ResponseEntity.ok(responseBody);
        }
        if(negotiationService.updateNegotiation(itemId, Id, dto) == 2){
            log.info(dto.toString());
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "제안의 상태가 변경되었습니다.");
            return ResponseEntity.ok(responseBody);
        }
        if(negotiationService.updateNegotiation(itemId, Id, dto) == 3){
            log.info(dto.toString());
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "구매가 확정되었습니다.");
            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/items/{itemId}/proposals/{proposalId}")
    public ResponseEntity<Map<String, String>> deleteNegotiation(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long id,
            @RequestBody NegotiationDeleteDto dto
    ) {
        if (negotiationService.deleteNegotiation(itemId, id, dto.getWriter(), dto.getPassword())) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "제안을 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
