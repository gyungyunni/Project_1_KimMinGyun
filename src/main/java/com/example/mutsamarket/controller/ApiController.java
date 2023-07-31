package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.salesItemDto.SalesItemEnrollDto;
import com.example.mutsamarket.dto.salesItemDto.SalesItemReadDto;
import com.example.mutsamarket.service.MarketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/mutsamarket")
@RestController
@Slf4j
@RequiredArgsConstructor
public class ApiController {
    private final MarketService marketService;

    @PostMapping("/items")
    public ResponseEntity<Map<String, String>> enroll(
            @Valid @RequestBody SalesItemEnrollDto dto,
            Authentication authentication
    ) {
        marketService.enrollSalesItem(dto, authentication);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "등록이 완료되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/items/{itemId}")
    public SalesItemReadDto read(
            @PathVariable("itemId") Long itemId
    ){
        return marketService.readItem(itemId);
    }
    @GetMapping("/items/all")
    public List<SalesItemReadDto> readAll(){
        return marketService.readItemAll();
    }

    @GetMapping("/items")
    public Page<SalesItemReadDto> readPage(
            @RequestParam(value = "page", defaultValue = "0") Long page,
            @RequestParam(value = "limit", defaultValue = "20") Long limit
    ){
        return marketService.readPageItem(page, limit);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Map<String, String>> updateItem(
            @PathVariable("itemId") Long itemId,
            @RequestBody SalesItemEnrollDto dto,
            Authentication authentication
    ) {
        marketService.updateSalesItem(itemId, dto, authentication);

        log.info(dto.toString());
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "물품이 수정되었습니다.");

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping(
            value= "/items/{itemId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public SalesItemEnrollDto updateImage(
                                 @PathVariable("itemId") Long id,
                                 @RequestParam("image") MultipartFile Image,
                                 Authentication authentication
    ){
        return marketService.updateMarketImage(Image , id, authentication);
    }
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, String>> deleteItem(
            @PathVariable("itemId") Long itemId,
            Authentication authentication
    ) {
        if (marketService.deleteItem(itemId, authentication)) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "물품을 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
