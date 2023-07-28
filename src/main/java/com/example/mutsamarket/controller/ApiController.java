package com.example.mutsamarket.controller;

import com.example.mutsamarket.dto.commentDto.CommentEnrollDto;
import com.example.mutsamarket.dto.salesItemDto.SalesItemDeleteDto;
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
            @Valid @RequestBody SalesItemEnrollDto dto
    ) {
        marketService.enrollSalesItem(dto);

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
            @RequestBody SalesItemEnrollDto dto
    ) {
        marketService.updateSalesItem(itemId, dto);

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
                                 @RequestParam("writer") String writer,
                                 @RequestParam("password") String password,
                                 @RequestParam("image") MultipartFile Image
    ){
        return marketService.updateMarketImage(writer,password, Image , id);
    }
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, String>> deleteItem(
            @PathVariable("itemId") Long itemId,
            @RequestBody SalesItemDeleteDto dto
    ) {
        if (marketService.deleteItem(itemId, dto.getWriter(), dto.getPassword())) {

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "물품을 삭제했습니다.");

            return ResponseEntity.ok(responseBody);
        }
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

}
