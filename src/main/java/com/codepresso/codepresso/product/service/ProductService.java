package com.codepresso.codepresso.product.service;

import com.codepresso.codepresso.product.converter.ProductConverter;
import com.codepresso.codepresso.product.repository.ProductOptionRepository;
import com.codepresso.codepresso.product.repository.ProductRepository;
import com.codepresso.codepresso.review.converter.ReviewConverter;
import com.codepresso.codepresso.product.dto.ProductDetailResponse;
import com.codepresso.codepresso.product.dto.ProductListResponse;
import com.codepresso.codepresso.review.dto.ReviewListResponse;
import com.codepresso.codepresso.product.entity.Product;
import com.codepresso.codepresso.product.entity.ProductOption;
import com.codepresso.codepresso.product.entity.Review;
import com.codepresso.codepresso.member.repository.FavoriteRepository;
import com.codepresso.codepresso.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepo;
    private final ProductOptionRepository productOptRepo;
    private final FavoriteRepository favoriteRepo;
    private final ReviewRepository reviewRepo;
    private final ReviewConverter reviewConverter;
    private final ProductConverter productConverter;

    @Transactional
    public List<ProductListResponse> findProductsWithCategory() {
        return productRepo.findAllProductsAsDto();
    }

    @Transactional(readOnly = true)
    public List<ReviewListResponse> findProductReviews(Long productId) {
        List<Review> reviews = reviewRepo.findByProductReviews(productId);
        Double avgRating = reviewRepo.getAverageRatingByProduct(productId);

        return reviews.stream()
                .map(review -> reviewConverter.toDto(review, avgRating))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListResponse> findProductsRandom() {
        Pageable pageable = PageRequest.of(0, 4);
        return productRepo.findByProductRandom(pageable);
    }

    @Transactional
    public ProductDetailResponse findByProductId(Long productId) {
        Product product = productRepo.findProductById(productId);
        long favCount = favoriteRepo.countByProductId(productId);
        List<ProductOption> options = productOptRepo.findOptionByProductId(productId);

        return productConverter.toDetailDto(product, favCount, options);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse findByProductIdReadOnly(Long productId) {
        Product product = productRepo.findProductById(productId);
        long favCount = favoriteRepo.countByProductId(productId);
        List<ProductOption> options = productOptRepo.findOptionByProductId(productId);

        return productConverter.toDetailDto(product, favCount, options);
    }

    @Transactional(readOnly = true)
    public List<ProductListResponse> searchProductsByKeyword(String keyword) {
        List<Product> products = productRepo.findByProductNameContaining(keyword);

        return products.stream()
                .map(productConverter::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductListResponse> searchProductsByHashtags(List<String> hashtags) {
        return productRepo.findByHashtagsIn(hashtags, hashtags.size());
    }

}