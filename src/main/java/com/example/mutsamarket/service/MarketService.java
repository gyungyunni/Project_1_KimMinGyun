package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.salesItemDto.SalesItemEnrollDto;
import com.example.mutsamarket.dto.salesItemDto.SalesItemReadDto;
import com.example.mutsamarket.entity.Comment;
import com.example.mutsamarket.entity.SalesItem;
import com.example.mutsamarket.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {
    private final SalesItemRepository salesItemRepository;

    public void enrollSalesItem(SalesItemEnrollDto dto) {

        SalesItem newItem = new SalesItem();
        newItem.setTitle(dto.getTitle());
        newItem.setDescription(dto.getDescription());
        newItem.setMinPriceWanted(dto.getMinPriceWanted());
        newItem.setWriter(dto.getWriter());
        newItem.setPassword(dto.getPassword());
        newItem.setStatus("판매중");
        newItem = salesItemRepository.save(newItem);

    }

    public SalesItemReadDto readItem(Long itemId) {
        Optional<SalesItem> optionalSalesItem
                = salesItemRepository.findById(Math.toIntExact(itemId));

        if (optionalSalesItem.isPresent())
            // DTO로 전환 후 반환
            return SalesItemReadDto.fromEntity(optionalSalesItem.get());

            // 아니면 404
        else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
    public List<SalesItemReadDto> readItemAll() {

        List<SalesItem> salesItemList = salesItemRepository.findAll();
        if (salesItemList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        List<SalesItemReadDto> salesItemReadDtoList = new ArrayList<>();
        for (SalesItem salesItem : salesItemList) {
            SalesItemReadDto salesItemDto = SalesItemReadDto.fromEntity(salesItem);
            salesItemReadDtoList.add(salesItemDto);
        }

        return salesItemReadDtoList;

    }

    public Page<SalesItemReadDto> readPageItem(Long page, Long limit) {

        Pageable pageable = PageRequest.of(Math.toIntExact(page), Math.toIntExact(limit));
        Page<SalesItem> salesItemPage
                = salesItemRepository.findAll(pageable);

        Page<SalesItemReadDto> salesItemDtoPage = salesItemPage.map(SalesItemReadDto::fromEntity);

        return salesItemDtoPage;

    }

    public void updateSalesItem(
            Long itemId,
            SalesItemEnrollDto dto
    ) {
        Optional<SalesItem> optionalSalesItem = salesItemRepository.findByWriterAndPasswordAndId(dto.getWriter(), dto.getPassword(), itemId);
        if (optionalSalesItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        else {
            SalesItem updateItem = optionalSalesItem.get();
            updateItem.setTitle(dto.getTitle());
            updateItem.setDescription(dto.getDescription());
            updateItem.setMinPriceWanted(dto.getMinPriceWanted());
            salesItemRepository.save(updateItem);
        }
    }

    public SalesItemEnrollDto updateMarketImage(String writer, String password, MultipartFile Image, Long id) {
        Optional<SalesItem> optionalMarket = salesItemRepository.findByWriterAndPasswordAndId(writer, password, id);
        if (optionalMarket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        SalesItem salesItem = optionalMarket.get();
        if (!(password.equals(salesItem.getPassword()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            // 2-1. 폴더만 만드는 과정
            String profileDir = String.format("media/%d/", id);
            log.info(profileDir);
            try {
                Files.createDirectories(Path.of(profileDir));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // 2-2. 확장자를 포함한 이미지 이름 만들기
            String originalFilename = Image.getOriginalFilename();
            String[] fileNameSplit = originalFilename.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            String profileFilename = "items." + extension;
            log.info(profileFilename);

            // 2-3. 폴더와 파일 경로를 포함한 이름 만들기
            String profilePath = profileDir + profileFilename;
            log.info(profilePath);

            // 3. MultipartFile 을 저장하기
            try {
                Image.transferTo(Path.of(profilePath));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            // 4. UserEntity 업데이트 (정적 프로필 이미지를 회수할 수 있는 URL)
            log.info(String.format("/static/%d/%s", id, profileFilename));

            salesItem.setImageUrl(String.format("/static/%d/%s", id, profileFilename));
            return SalesItemEnrollDto.fromEntity(salesItemRepository.save(salesItem));

        }
    }

    public boolean deleteItem(Long id, String writer, String password) {
        Optional<SalesItem> optionalSalesItem
                = salesItemRepository.findByIdAndWriterAndPassword(id, writer, password);

        if (optionalSalesItem.isPresent()) {
            // DTO로 전환 후 반환
            salesItemRepository.delete(optionalSalesItem.get());
            // 아니면 404
            return true;
        } else return false;

    }

}