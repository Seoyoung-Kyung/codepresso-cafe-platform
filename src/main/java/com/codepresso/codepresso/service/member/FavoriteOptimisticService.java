package com.codepresso.codepresso.service.member;

import com.codepresso.codepresso.entity.member.Favorite;
import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.repository.member.FavoriteRepository;
import com.codepresso.codepresso.repository.member.ProductConcurrencyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteOptimisticService {
    private final FavoriteService favoriteService;
    private final ProductConcurrencyRepository productRepository;

    @Transactional
    public int addFavoriteWithRetry(Long memberId, Long productId, int maxRetries) {
        int retryCount = 0;

        while (retryCount <= maxRetries) {
            try {
                favoriteService.addFavoriteTest(memberId, productId);
                return retryCount;
            } catch(DataIntegrityViolationException | JpaSystemException e) {

                retryCount++;
                if(retryCount >= maxRetries) {
                    throw new RuntimeException("최대 재시도 횟수 초과", e);
                }

                try {
                    Thread.sleep(10);
                } catch(InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 중 인터럽트 발생", ie);
                }
            }
        }
        return retryCount;
    }


    @Transactional
    public Long getFavoriteCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        return product.getFavoriteCount();
    }

}
