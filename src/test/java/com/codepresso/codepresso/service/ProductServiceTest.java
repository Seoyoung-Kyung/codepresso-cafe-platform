package com.codepresso.codepresso.service;

import com.codepresso.codepresso.repository.product.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceTest {

    private static final Logger log = LoggerFactory.getLogger(ProductServiceTest.class);
//    @Autowired
//    private com.codepresso.codepresso.service.ProductService productService;
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Test
//    public void 상품목록() {
//        // given
//        Product product = Product.builder()
//                .setProductId(1).build();
//        ProductResponseDTO product = new ProductResponseDTO();
//        product.setCategoryCode("COFFEE");
//
//        // when
//
//        // then
//    }

}