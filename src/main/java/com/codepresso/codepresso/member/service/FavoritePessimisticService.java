package com.codepresso.codepresso.member.service;

import com.codepresso.codepresso.member.entity.Favorite;
import com.codepresso.codepresso.member.repository.FavoriteRepository;
import com.codepresso.codepresso.member.repository.ProductConcurrencyRepository;
import com.codepresso.codepresso.product.entity.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritePessimisticService {

    private final FavoriteRepository favoriteRepository;
    private final ProductConcurrencyRepository productRepository;

    @Transactional
    public void addFavorite(Long memberId, Long productId) {

        try {
            Product product = productRepository.findByWithPessimisticLock(productId)
                    .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));

            Favorite favorite = Favorite.builder()
                    .memberId(memberId)
                    .productId(productId)
                    .orderby(1)
                    .build();

            favoriteRepository.saveAndFlush(favorite);

            product.increaseFavoriteCount();
            productRepository.saveAndFlush(product); // ← 즉시 DB 반영

            log.debug("즐겨찾기 추가 성공 : memberId = {}, productId = {}", memberId, productId);
            log.debug("메서드 종료 직전: memberId = {}, productId = {}", memberId, productId);

        } catch (DataIntegrityViolationException | JpaSystemException e) {
            log.debug("이미 존재하는 즐겨찾기 : memberId = {}, productId = {}", memberId, productId);
        } catch (Exception e) {
            // 정확한 예외 타입 확인
            log.error("=".repeat(50));
            log.error("예외 발생!");
            log.error("예외 타입: {}", e.getClass().getName());
            log.error("예외 메시지: {}", e.getMessage());

            // 예외 재발생
            throw e;
        }
    }

    @Transactional
    public Long getFavoriteCount(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        return product.getFavoriteCount();
    }
}
