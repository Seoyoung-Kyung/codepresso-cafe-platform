package com.codepresso.codepresso.service.member;

import com.codepresso.codepresso.entity.member.Favorite;
import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.repository.member.FavoriteConcurrencyRepository;
import com.codepresso.codepresso.repository.product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritePessimisticService {

    private final FavoriteConcurrencyRepository favoriteRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addFavorite(Long memberId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

        Optional<Favorite> existingFavorite = favoriteRepository.findByMemberIdAndProductIdWithPessimisticLock(memberId, productId);

        if(existingFavorite.isEmpty()) {
            Favorite favorite = Favorite.builder()
                    .memberId(memberId)
                    .productId(productId)
                    .orderby(1)
                    .build();

            favoriteRepository.save(favorite);
            log.debug("즐겨찾기 추가 성공 : memberId = {}, productId = {}", memberId, productId);
        } else {
            log.debug("이미 존재하는 즐겨찾기 : memberId = {}, productId = {}", memberId, productId);
        }
    }

}
