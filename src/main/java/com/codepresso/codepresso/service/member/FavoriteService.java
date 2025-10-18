package com.codepresso.codepresso.service.member;

import com.codepresso.codepresso.dto.AuthResponse;
import com.codepresso.codepresso.dto.member.FavoriteListResponse;
import com.codepresso.codepresso.dto.member.FavoriteRequest;
import com.codepresso.codepresso.dto.member.FavoriteResponse;
import com.codepresso.codepresso.entity.member.Favorite;
import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.repository.member.FavoriteRepository;
import com.codepresso.codepresso.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * 즐겨찾기 관련 비즈니스 로직 서비스
 * 즐겨찾기추가, 즐겨찾기목록, 즐겨찾기삭제 기능 담당
 */
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    /**
     * 즐겨찾기 추가
     * 회원이 상품을 즐겨찾기에 추가
     * 
     * @param memberId 회원 ID
     * @param request 즐겨찾기 추가 요청
     * @return AuthResponse 성공/실패 응답
     */
    @Transactional
    public AuthResponse addFavorite(Long memberId, FavoriteRequest request) {
        try {
            // 상품 존재 여부 확인
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NoSuchElementException("상품을 찾을 수 없습니다. ID: " + request.getProductId()));

            // 이미 즐겨찾기에 추가된 상품인지 확인
            if (favoriteRepository.existsByMemberIdAndProductId(memberId, request.getProductId())) {
                return AuthResponse.builder()
                        .success(false)
                        .message("이미 즐겨찾기에 추가된 상품입니다.")
                        .build();
            }

            // 정렬 순서 설정 (요청에 없으면 현재 즐겨찾기 개수 + 1)
            Integer orderby = request.getOrderby();
            if (orderby == null) {
                orderby = (int) favoriteRepository.countByMemberId(memberId) + 1;
            }

            // 즐겨찾기 생성 및 저장
            Favorite favorite = Favorite.builder()
                    .memberId(memberId)
                    .productId(request.getProductId())
                    .orderby(orderby)
                    .build();

            favoriteRepository.save(favorite);

            return AuthResponse.builder()
                    .success(true)
                    .message("즐겨찾기에 추가되었습니다.")
                    .build();

        } catch (NoSuchElementException e) {
            return AuthResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("즐겨찾기 추가 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 즐겨찾기 목록 조회
     * 회원의 즐겨찾기 목록을 상품 정보와 함께 조회
     * 
     * @param memberId 회원 ID
     * @return FavoriteListResponse 즐겨찾기 목록 응답
     */
    @Transactional(readOnly = true)
    public FavoriteListResponse getFavoriteList(Long memberId) {
        try {
            // 즐겨찾기 목록과 상품 정보를 함께 조회
            List<Object[]> results = favoriteRepository.findFavoritesWithProductByMemberId(memberId);
            
            // DTO로 변환
            List<FavoriteResponse> favorites = results.stream()
                    .map(this::convertToFavoriteResponse)
                    .collect(Collectors.toList());

            return FavoriteListResponse.builder()
                    .favorites(favorites)
                    .totalCount(favorites.size())
                    .build();

        } catch (Exception e) {
            return FavoriteListResponse.builder()
                    .favorites(List.of())
                    .totalCount(0)
                    .build();
        }
    }

    /**
     * 즐겨찾기 삭제
     * 회원의 특정 상품을 즐겨찾기에서 제거
     * 
     * @param memberId 회원 ID
     * @param productId 삭제할 상품 ID
     * @return AuthResponse 성공/실패 응답
     */
    @Transactional
    public AuthResponse removeFavorite(Long memberId, Long productId) {
        try {
            // 즐겨찾기 존재 여부 확인
            Favorite favorite = favoriteRepository.findByMemberIdAndProductId(memberId, productId);
            if (favorite == null) {
                return AuthResponse.builder()
                        .success(false)
                        .message("즐겨찾기에 등록되지 않은 상품입니다.")
                        .build();
            }

            // 즐겨찾기 삭제
            favoriteRepository.delete(favorite);

            return AuthResponse.builder()
                    .success(true)
                    .message("즐겨찾기에서 삭제되었습니다.")
                    .build();

        } catch (Exception e) {
            return AuthResponse.builder()
                    .success(false)
                    .message("즐겨찾기 삭제 중 오류가 발생했습니다: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Object[]를 FavoriteResponse로 변환
     * 
     * @param result [Favorite, Product] 배열
     * @return FavoriteResponse 변환된 DTO
     */
    private FavoriteResponse convertToFavoriteResponse(Object[] result) {
        Favorite favorite = (Favorite) result[0];
        Product product = (Product) result[1];

        return FavoriteResponse.builder()
                .memberId(favorite.getMemberId())
                .productId(favorite.getProductId())
                .productName(product.getProductName())
                .productContent(product.getProductContent())
                .productPhoto(product.getProductPhoto())
                .price(product.getPrice())
                .orderby(favorite.getOrderby())
                .createdAt(LocalDateTime.now()) // 실제로는 엔티티에서 가져와야 함
                .build();
    }
}
