package com.codepresso.codepresso.cart.converter;

import com.codepresso.codepresso.cart.dto.CartItemResponse;
import com.codepresso.codepresso.cart.dto.CartOptionResponse;
import com.codepresso.codepresso.cart.dto.CartResponse;
import com.codepresso.codepresso.cart.entity.CartItem;
import com.codepresso.codepresso.cart.entity.CartOption;
import com.codepresso.codepresso.product.entity.Product;
import com.codepresso.codepresso.product.entity.ProductOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartConverter {

    public CartResponse toCartResponse(Long cartId, Long memberId, List<CartItem> items) {
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toCartItemResponse)
                .toList();

        return CartResponse.builder()
                .cartId(cartId)
                .memberId(memberId)
                .items(itemResponses)
                .build();
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        List<CartOptionResponse> optionResponses = item.getOptions().stream()
                .filter(this::hasValidOption)
                .map(this::toCartOptionResponse)
                .toList();

        return CartItemResponse.builder()
                .cartItemId(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getProductName())
                .productPhoto(item.getProduct().getProductPhoto())
                .quantity(item.getQuantity())
                .price(calcTotalPrice(item))
                .options(optionResponses)
                .build();
    }

    private boolean hasValidOption(CartOption cartOption) {
        ProductOption po = cartOption.getProductOption();
        return po != null
                && po.getOptionStyle() != null
                && po.getOptionStyle().getOptionStyle() != null
                && !po.getOptionStyle().getOptionStyle().trim().equals("기본");
    }

    private CartOptionResponse toCartOptionResponse(CartOption cartOption) {
        ProductOption po = cartOption.getProductOption();
        return CartOptionResponse.builder()
                .optionId(po.getId())
                .extraPrice(safeExtraPrice(po))
                .optionStyle(po.getOptionStyle().getOptionStyle())
                .build();
    }

    private int calcTotalPrice(CartItem item) {
        Integer unitPrice = item.getPrice();
        int price = (unitPrice != null) ? unitPrice : calcUnitPrice(item.getProduct(), item.getOptions());
        int qty = (item.getQuantity() != null) ? item.getQuantity() : 0;
        return price * qty;
    }

    private int calcUnitPrice(Product product, List<CartOption> cartOptions) {
        int basePrice = requireProductPrice(product);
        if (cartOptions == null || cartOptions.isEmpty()) {
            return basePrice;
        }
        int extra = cartOptions.stream()
                .map(CartOption::getProductOption)
                .mapToInt(this::safeExtraPrice)
                .sum();
        return basePrice + extra;
    }

    private int safeExtraPrice(ProductOption productOption) {
        if (productOption == null || productOption.getOptionStyle() == null) {
            return 0;
        }
        return productOption.getOptionStyle().getExtraPrice();
    }

    private int requireProductPrice(Product product) {
        Integer price = product.getPrice();
        if (price == null) {
            throw new IllegalArgumentException(
                    "상품 가격이 설정되어 있지 않습니다. productId: " + product.getId());
        }
        return price;
    }
}
