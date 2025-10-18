package com.codepresso.codepresso.controller.product;

import com.codepresso.codepresso.dto.product.ProductDetailResponse;
import com.codepresso.codepresso.dto.product.ProductListResponse;
import com.codepresso.codepresso.service.product.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductViewController {

    private final ProductService productService;

    /**
     * 상품 목록 페이지
     */
    @GetMapping
    public String productList(Model model) {
        List<ProductListResponse> products = productService.findProductsByCategory();
        model.addAttribute("products", products);
        return "product/productList";
    }

    /**
     * 상품 상세 페이지
     */
    @GetMapping("/{productId}")
    public String getProductDetail(@PathVariable Long productId, Model model) {
        try {
            ProductDetailResponse product = productService.findByProductId(productId);
            model.addAttribute("product", product);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "product/productDetail";
    }

    /**
     * 상품별 리뷰 조회
     */
    @GetMapping("/{productId}/reviews")
    public String getProductReviews(@PathVariable Long productId, Model model) {
        try {
            ProductDetailResponse product = productService.findByProductId(productId);
            model.addAttribute("product", product);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "product/productReviews";
    }

    @GetMapping("/search")
    public String searchProducts(HttpServletResponse response) {
        // 정적 리소스 캐싱 설정 (1시간)
        response.setHeader("Cache-Control", "public, max-age=3600");
        return "product/productSearch";
    }
}