package com.codepresso.codepresso.product.controller;

import com.codepresso.codepresso.product.dto.ProductListResponse;
import com.codepresso.codepresso.review.dto.ReviewListResponse;
import com.codepresso.codepresso.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 전체 상품 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ProductListResponse>> getAllProducts() {
        List<ProductListResponse> products = productService.findProductsByCategory();
//        List<ProductListResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 상품 랜덤 추천 (4개)
     */
    @GetMapping("/random")
    public ResponseEntity<List<ProductListResponse>> getProductRandom() {
        List<ProductListResponse> products = productService.findProductsRandom();
        if (products.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(products);
    }

    /**
     * 상품 리뷰 목록 조회
     */
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ReviewListResponse>> getProductReviews(
            @PathVariable Long productId) {
        List<ReviewListResponse> reviews = productService.findProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 상품 검색(keyword)
     */
    @PostMapping("/search/keyword")
    public ResponseEntity<List<ProductListResponse>> searchProductsByKeyword(@RequestParam String keyword) {
        List<ProductListResponse> products = productService.searchProductsByKeyword(keyword);
        return ResponseEntity.ok(products);
    }

    /**
     * 상품 검색(multiple hashtags)
     */
    @PostMapping("/search/hashtags")
    public ResponseEntity<List<ProductListResponse>> searchProductsByHashtags(@RequestParam List<String> hashtags) {
        List<ProductListResponse> products = productService.searchProductsByHashtags(hashtags);
        return ResponseEntity.ok(products);
    }

}