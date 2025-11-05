package com.codepresso.codepresso.member.service;

import com.codepresso.codepresso.member.entity.Favorite;
import com.codepresso.codepresso.member.repository.FavoriteRepository;
import com.codepresso.codepresso.member.repository.ProductConcurrencyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteAtomicService {

    private final FavoriteRepository favoriteRepository;
    private final ProductConcurrencyRepository productConcurrencyRepository;

    @Transactional
    public void addFavoriteWithAtomicUpdate(Long memberId, Long productId) {

        if(!favoriteRepository.existsByMemberIdAndProductId(memberId, productId)) {
            Favorite favorite = Favorite.builder()
                    .memberId(memberId)
                    .productId(productId)
                    .orderby(1)
                    .build();

            favoriteRepository.save(favorite);

            int updated = productConcurrencyRepository.incrementFavoriteCountAtomic(productId);
            if(updated == 0) {
                throw new IllegalStateException("상품이 존재하지 않습니다.");
            }
        }
    }


}
