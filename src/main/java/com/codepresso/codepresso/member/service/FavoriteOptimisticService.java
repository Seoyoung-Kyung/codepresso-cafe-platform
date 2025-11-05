package com.codepresso.codepresso.member.service;

import com.codepresso.codepresso.member.repository.ProductConcurrencyRepository;
import com.codepresso.codepresso.product.entity.Product;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteOptimisticService {
    private final FavoriteService favoriteService;
    private final ProductConcurrencyRepository productRepository;

    private static final long RETRY_DELAY_MS = 50;

    public void addFavoriteWithRetry(Long memberId, Long productId) throws InterruptedException {
        int retryCount = 0;
        int maxRetries = 10;

        while (retryCount < maxRetries) {
            try {
                favoriteService.addFavoriteTest(memberId, productId);
                log.info("즐겨찾기 성공 - productId: {}, memberId: {}, 총 시도횟수: {}",
                        productId,memberId, retryCount + 1);
                return;
            } catch (Exception e) {
                retryCount++;

                if (retryCount >= maxRetries) {
                    throw e; // 재시도 한계 도달 시 예외 던짐
                }
                log.warn("즐겨찾기 재시도 - productId: {}, memberId: {}, 현재 시도횟수: {}, error: {}",
                        productId,memberId, retryCount, e.getMessage());
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
    }


    @Transactional
    public Long getFavoriteCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        Long count = product.getFavoriteCount();
        return count != null ? count : 0L;
    }

}
