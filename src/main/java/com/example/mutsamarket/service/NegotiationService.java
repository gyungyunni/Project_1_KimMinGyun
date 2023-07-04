package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.negotiationDto.NegotiationEnrollDto;
import com.example.mutsamarket.dto.negotiationDto.NegotiationReadDto;
import com.example.mutsamarket.entity.Negotiation;
import com.example.mutsamarket.entity.SalesItem;
import com.example.mutsamarket.repository.NegotiationRepository;
import com.example.mutsamarket.repository.SalesItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final SalesItemRepository salesItemRepository;
    public void enrollNegotiation(NegotiationEnrollDto dto , Long itemId) {

        Negotiation newNego = new Negotiation();
        newNego.setWriter(dto.getWriter());
        newNego.setPassword(dto.getPassword());
        newNego.setSuggestedPrice(dto.getSuggestedPrice());
        newNego.setSalesItem(salesItemRepository.findById(itemId));
        newNego.setStatus("제안");
        newNego = negotiationRepository.save(newNego);
    }

    public Page<NegotiationReadDto> readNegoPage(Long page, Long itemId, String writer, String password) {
        Pageable pageable = PageRequest.of(Math.toIntExact(page), 25);

        // 대상 물품의 주인
        if(negotiationRepository.findTopBySalesItemWriterAndSalesItemPassword(writer,password).isPresent()) {
            Page<Negotiation> negoPage
                    = negotiationRepository.findAllBySalesItemId(itemId, pageable);

            return negoPage.map(NegotiationReadDto::fromEntity);
        }
        // 네고 등록한 사용자
        if(negotiationRepository.findTopByWriterAndPassword(writer,password).isPresent()){
            Page<Negotiation> negoPage
                    = negotiationRepository.findAllBySalesItemIdAndWriterAndPassword(itemId, writer,password, pageable);

            return negoPage.map(NegotiationReadDto::fromEntity);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public int updateNegotiation(
            Long itemId,
            Long id,
            NegotiationEnrollDto dto
    ) {

        String ok = "수락";
        String no = "거절";
        String done = "확정";
        if(dto.getStatus() == null) {
            // 아니면 로직 진행
            Optional<Negotiation> optionalNego = negotiationRepository.findBySalesItemIdAndIdAndWriterAndPassword(itemId, Math.toIntExact(id), dto.getWriter(), dto.getPassword());

            if (optionalNego.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Negotiation updateNego = optionalNego.get();
            updateNego.setSuggestedPrice(dto.getSuggestedPrice());
            updateNego = negotiationRepository.save(updateNego);
            return 1;
        }
        if((dto.getStatus().equals(ok)) || (dto.getStatus().equals(no))){

            // 아니면 로직 진행
            Optional<Negotiation> optionalNego = negotiationRepository.findBySalesItemIdAndIdAndSalesItemWriterAndSalesItemPassword(itemId, Math.toIntExact(id), dto.getWriter(), dto.getPassword());
            if (optionalNego.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Negotiation updateNego = optionalNego.get();
            updateNego.setStatus(dto.getStatus());
            updateNego = negotiationRepository.save(updateNego);
            return 2;
        }
        if(dto.getStatus().equals(done)){
            Optional<Negotiation> optionalNego = negotiationRepository.findBySalesItemIdAndIdAndWriterAndPasswordAndStatus(itemId, Math.toIntExact(id), dto.getWriter(), dto.getPassword(), ok);
            if (optionalNego.isEmpty()) {
                return 3;
            }
            Negotiation updateNego = optionalNego.get();
            updateNego.setStatus("확정");
            SalesItem salesItem = salesItemRepository.findById(itemId);
            salesItem.setStatus("판매완료");
            updateNego = negotiationRepository.save(updateNego);
            salesItem = salesItemRepository.save(salesItem);

            List<Negotiation> updateNegos = negotiationRepository.findAllBySalesItemIdAndIdNot(itemId, Math.toIntExact(id));
            updateNegos.forEach(negotiation -> negotiation.setStatus("거절"));
            negotiationRepository.saveAll(updateNegos);
            return 3;
        }
        else return 0;
    }

    public boolean deleteNegotiation(Long itemId, Long id, String writer, String password) {
        Optional<Negotiation> optionalNego
                = negotiationRepository.findBySalesItemIdAndIdAndWriterAndPassword(itemId, Math.toIntExact(id), writer, password);

        if (optionalNego.isPresent()) {
            // DTO로 전환 후 반환
            negotiationRepository.delete(optionalNego.get());
            // 아니면 404
            return true;
        } else return false;

    }

}
