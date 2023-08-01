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
import org.springframework.security.core.Authentication;
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
            @PathVariable("itemId") Long itemId,
            Authentication authentication
    ) {
        negotiationService.enrollNegotiation(dto, itemId, authentication);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "구매 제안이 등록되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/items/{itemId}/proposals")
    public Page<NegotiationReadDto> readNegoPage(
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @PathVariable("itemId") Long itemId,
            Authentication authentication
    ){
        return negotiationService.readNegoPage(page, itemId, authentication);
    }

    @PutMapping("/items/{itemId}/proposals/{proposalId}")
    public ResponseEntity<Map<String, String>> updateNegotiation(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long Id,
            @Valid @RequestBody NegotiationEnrollDto dto,
            Authentication authentication
    ) {
        int updateResult = negotiationService.updateNegotiation(itemId, Id, dto, authentication);

        Map<String, String> responseBody = new HashMap<>();
        switch (updateResult) {
            case 1 -> responseBody.put("message", "제안이 수정되었습니다.");
            case 2 -> responseBody.put("message", "제안의 상태가 변경되었습니다.");
            case 3 -> responseBody.put("message", "구매가 확정되었습니다.");
            default -> throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/items/{itemId}/proposals/{proposalId}")
    public ResponseEntity<Map<String, String>> deleteNegotiation(
            @PathVariable("itemId") Long itemId,
            @PathVariable("proposalId") Long id,
            Authentication authentication
    ) {
        if (negotiationService.deleteNegotiation(itemId, id, authentication)) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "제안을 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
