package com.example.mutsamarket.service;

import com.example.mutsamarket.dto.negotiationDto.NegotiationEnrollDto;
import com.example.mutsamarket.dto.negotiationDto.NegotiationReadDto;
import com.example.mutsamarket.entity.Negotiation;
import com.example.mutsamarket.entity.SalesItem;
import com.example.mutsamarket.entity.UserEntity;
import com.example.mutsamarket.repository.NegotiationRepository;
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

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NegotiationService {

    private final NegotiationRepository negotiationRepository;
    private final SalesItemRepository salesItemRepository;
    private final UserRepository userRepository;
    public void enrollNegotiation(NegotiationEnrollDto dto , Long itemId, Authentication authentication) {

        String check = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(check);
        UserEntity user = optionalUser.get();

        Negotiation newNego = new Negotiation();
        newNego.setSuggestedPrice(dto.getSuggestedPrice());
        newNego.setSalesItem(salesItemRepository.findById(itemId));
        newNego.setStatus("제안");
        newNego.setUser(user);
        newNego = negotiationRepository.save(newNego);
    }

    public Page<NegotiationReadDto> readNegoPage(Long page, Long itemId, Authentication authentication) {

        String username = authentication.getName();

        Pageable pageable = PageRequest.of(Math.toIntExact(page), 25);

        // 대상 물품의 주인, 모든 구매제안 확인 가능
        // salesItem 레포지토리에 판매 아이템 id와 현재 로그인한 사용자의 username이 일차하는게 있다면
        // == 조회하려는 사람이 현재 로그인한 사용자이고, 판매 게시글도 있다는 뜻
        if(salesItemRepository.findByItemIdAndUsername(itemId,username).isPresent()) {
            // 그렇다면 그 판매 아이템에 해당하는 네고들을 다 보여줌
            Page<Negotiation> negoPage
                    = negotiationRepository.findAllBySalesItemId(itemId, pageable);

            return negoPage.map(NegotiationReadDto::fromEntity);
        }
        // 현재 로그인한 사용자가 네고 등록한 사용자인지, 내가 등록한 네고만 확인이 가능
        else if(negotiationRepository.countByUsername(username) > 0){
            Page<Negotiation> negoPage
                    = negotiationRepository.findAllBySalesItemIdAndUsername(itemId, username , pageable);
            return negoPage.map(NegotiationReadDto::fromEntity);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public int updateNegotiation(
            Long itemId,
            Long id,
            NegotiationEnrollDto dto,
            Authentication authentication
    ) {

        String ok = "수락";
        String no = "거절";
        String done = "확정";

        String username = authentication.getName();

        if (dto.getStatus() == null) {
            // 아니면 로직 진행

            Optional<Negotiation> optionalNego = negotiationRepository.findByNegotiationIdAndSalesItemIdAndUsername(id, itemId, username);

            if (optionalNego.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }
            Negotiation updateNego = optionalNego.get();
            updateNego.setSuggestedPrice(dto.getSuggestedPrice());
            updateNego = negotiationRepository.save(updateNego);
            return 1;
        }
        if ((dto.getStatus().equals(ok) || dto.getStatus().equals(no))) { // "아이템 판매자가" 네고를 수락이나 거절하는
            if (salesItemRepository.findById(itemId).getUser().getUsername().equals(username)) {
                Optional<Negotiation> optionalNego = negotiationRepository.findByNegotiationIdAndSalesItemId(id, itemId);
                if (optionalNego.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND);
                }
                Negotiation updateNego = optionalNego.get();
                updateNego.setStatus(dto.getStatus());
                updateNego = negotiationRepository.save(updateNego);
                return 2;
            }
        }
        if (dto.getStatus().equals(done)) {
            // 확정요청을 보내면
            Optional<Negotiation> optionalNego = negotiationRepository.findBySalesItemIdAndIdAndUsernameAndStatus(itemId, id, username, ok);
            // 수락 상태로 되어있는 걸 찾음
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

        return 0;
    }

    public boolean deleteNegotiation(Long itemId, Long id, Authentication authentication) {

        String username = authentication.getName();

        Optional<Negotiation> optionalNego
                = negotiationRepository.findByNegotiationIdAndSalesItemIdAndUsername(id, itemId, username);

        if (optionalNego.isPresent()) {
            // DTO로 전환 후 반환
            negotiationRepository.delete(optionalNego.get());
            // 아니면 404
            return true;
        } else return false;

    }

}
