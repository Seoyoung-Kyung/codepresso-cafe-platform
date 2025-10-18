package com.codepresso.codepresso.service.cart;

import com.codepresso.codepresso.dto.cart.CartOptionResponse;
import com.codepresso.codepresso.dto.cart.CartItemResponse;
import com.codepresso.codepresso.dto.cart.CartResponse;
import com.codepresso.codepresso.entity.cart.Cart;
import com.codepresso.codepresso.entity.cart.CartItem;
import com.codepresso.codepresso.entity.cart.CartOption;
import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.entity.product.ProductOption;
import com.codepresso.codepresso.repository.product.ProductOptionRepository;
import com.codepresso.codepresso.repository.product.ProductRepository;
import com.codepresso.codepresso.repository.cart.CartItemRepository;
import com.codepresso.codepresso.repository.cart.CartOptionRepository;
import com.codepresso.codepresso.repository.cart.CartRepository;
import com.codepresso.codepresso.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartOptionRepository cartOptionRepository;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final MemberRepository memberRepository;


    // c - 아이템 추가(동일 상품+옵션 묶음이면 수량 증가, 아니면 새 행 추가)
    public CartItem addItemWithOptions(Long memberId, Long productId, int quantity, List<Long> optionIds) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다. memberId=" + memberId));

        // 장바구니 조회 (없으면 새로 생성)
        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setMember(member);
                    return cartRepository.save(newCart);
                });

        // 상품 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 없습니다. productId=" + productId));

        // 옵션 중복 검증
        if (optionIds != null && !optionIds.isEmpty()) {
            long distinctCount = optionIds.stream().distinct().count();
            if (distinctCount != optionIds.size()) {
                throw new IllegalArgumentException("중복 옵션은 허용되지 않습니다.");
            }
        }

        // 옵션 조회
        List<ProductOption> requestedOptions = (optionIds == null || optionIds.isEmpty())
                ? List.of()
                : productOptionRepository.findAllById(optionIds);

        if (requestedOptions.size() != (optionIds == null ? 0 : optionIds.size())) {
            throw new IllegalArgumentException("존재하지 않는 옵션이 포함되어 있습니다.");
        }

        for (ProductOption op : requestedOptions) {
            if (!op.getProduct().getId().equals(productId)) {
                throw new IllegalArgumentException("다른 상품의 옵션이 포함되어 있습니다. optionId=" + op.getId());
            }
        }

        // 기존 동일 상품 아이템 확인
        List<CartItem> existingItems = cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId);
        Set<Long> requestedOptionIdSet = (optionIds == null) ? Set.of() : new HashSet<>(optionIds);

        for (CartItem existingItem : existingItems) {
            if (sameOptionSet(existingItem, requestedOptionIdSet)) {
                // 같은 옵션 묶음 → 수량만 증가
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                // 단가 정합성 보장(혹시 옵션 단가 정책이 바뀌었을 수 있으니 재동기화)
                int unitPrice = calcUnitPriceFromCartOptions(product, existingItem.getOptions());
                existingItem.setPrice(unitPrice);
                return existingItem;
            }
        }

        // 새로운 CartItem 생성 (단가 먼저 계산해서 price에 저장)
        int unitPrice = calcUnitPriceFromProductOptions(product, requestedOptions);

        CartItem newItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .price(unitPrice) // <-- 단가
                .build();

        CartItem saved = cartItemRepository.save(newItem);

        // 옵션 연결 (연관관계 컬렉션도 같이 세팅)
        for (ProductOption productOption : requestedOptions) {
            CartOption cartOption = new CartOption();
            cartOption.setCartItem(saved);
            cartOption.setProductOption(productOption);
            cartOptionRepository.save(cartOption);
            saved.getOptions().add(cartOption);
        }

        return saved;
    }

    private boolean sameOptionSet(CartItem existingCartItem, Set<Long> requestedOptionIdSet){
        List<CartOption> existingCartOptions = existingCartItem.getOptions();
        if (existingCartOptions == null || existingCartOptions.isEmpty()) {
            return requestedOptionIdSet.isEmpty();
        }
        if (existingCartOptions.size() != requestedOptionIdSet.size()) {
            return false;
        }
        Set<Long> existingOptionIdSet = existingCartOptions.stream()
                .map(cartOption -> cartOption.getProductOption().getId())
                .collect(Collectors.toSet());

        return existingOptionIdSet.equals(requestedOptionIdSet);
    }

    // 가격 Null 방지 (상품 기본가)
    private int requireProductPrice(Product product) {
        Integer price = product.getPrice();
        if (price == null) {
            throw new IllegalArgumentException("상품 가격이 설정되어 있지 않습니다. productId : " + product.getId());
        }
        return price;
    }

    // (안전) 옵션 추가금 추출
    private int safeExtraPrice(ProductOption productOption) {
        // optionStyle 혹은 extraPrice가 null이어도 안전하게 0으로 처리
        if (productOption == null ||
                productOption.getOptionStyle() == null ) {
            return 0;
        }
        return productOption.getOptionStyle().getExtraPrice();
    }

    // 단가 계산 = 상품 기본가 + 옵션 추가금 합
    private int calcUnitPriceFromProductOptions(Product product, List<ProductOption> productOptions) {
        int basePrice = requireProductPrice(product);
        int extra = 0;
        if (productOptions != null && !productOptions.isEmpty()) {
            extra = productOptions.stream()
                    .mapToInt(this::safeExtraPrice)
                    .sum();
        }
        return basePrice + extra;
    }

    // 단가 계산
    private int calcUnitPriceFromCartOptions(Product product, List<CartOption> cartOptions) {
        int basePrice = requireProductPrice(product);
        int extra = 0;
        if (cartOptions != null && !cartOptions.isEmpty()) {
            extra = cartOptions.stream()
                    .map(CartOption::getProductOption)
                    .mapToInt(this::safeExtraPrice)
                    .sum();
        }
        return basePrice + extra;
    }

    // 총액 계산(단가 × 수량)
    private int calcTotalPrice(CartItem item) {
        Integer unitPrice = item.getPrice();
        int price = (unitPrice == null) ? calcUnitPriceFromCartOptions(item.getProduct(), item.getOptions()) : unitPrice;
        Integer quantity = item.getQuantity();
        int qty = (quantity == null) ? 0 : quantity;
        return price * qty;
    }

    // r - 특정 장바구니 안의 모든 아이템 조회 (DTO 변환 포함)
    @Transactional(readOnly = true)
    public CartResponse getCartByMemberId(Long memberId) {

        // 장바구니 조회
        Cart cart = cartRepository.findByMemberIdWithItems(memberId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 없습니다. memberId : " + memberId));

        // cartItem -> DTO 변환
        List<CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> {
                    // CartItemOption -> DTO 변환
                    List<CartOptionResponse> optionResponses = item.getOptions().stream()
                            .filter(co -> co.getProductOption() != null
                                    && co.getProductOption().getOptionStyle() != null
                                    && co.getProductOption().getOptionStyle().getOptionStyle() != null
                                    && !co.getProductOption().getOptionStyle().getOptionStyle().trim().equals("기본"))
                            .map(cartOption -> CartOptionResponse.builder()
                                    .optionId(cartOption.getProductOption().getId())
                                    .extraPrice(safeExtraPrice(cartOption.getProductOption()))
                                    .optionStyle(cartOption.getProductOption().getOptionStyle().getOptionStyle())
                                    .build())
                            .toList();

                    int totalPrice = calcTotalPrice(item); // 단가*수량

                    return CartItemResponse.builder()
                            .cartItemId(item.getId())
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getProductName())
                            .productPhoto(item.getProduct().getProductPhoto())
                            .quantity(item.getQuantity())
                            .price(totalPrice) // 응답엔 총액을 내려줌
                            .options(optionResponses)
                            .build();
                })
                .toList();

        return CartResponse.builder()
                .cartId(cart.getId())
                .memberId(cart.getMember().getId())
                .items(itemResponses)
                .build();
    }

    // u - 수량 newQuantity로 덮어쓰기 (단가 보존)
    public void changeItemQuantity(Long cartItemId, Long memberId, int newQuantity){
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "장바구니에 아이템이 없습니다. cartItemId = " + cartItemId ));

        // 회원 검증
        if (!item.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인 장바구니 아이템만 수정할 수 있습니다.");
        }

        item.setQuantity(newQuantity);
    }

    // d - cartItem 삭제
    public void deleteItem(Long cartItemId, Long memberId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 아이템이 없습니다. cartItemId :" + cartItemId));

        // 소유권 검증
        if (!item.getCart().getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인 장바구니 아이템만 삭제할 수 있습니다.");
        }

        // 옵션 먼저 삭제
        cartOptionRepository.deleteByCartItemId(item.getId());

        // 아이템 삭제
        cartItemRepository.deleteById(item.getId());
    }

    // d - 전체 삭제
    @Transactional
    public void clearCart(Long memberId, Long cartId) {
        // cartId가 본인 소유인지 검증
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 없습니다. cartId = " + cartId));

        if (!cart.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인 장바구니만 비울 수 있습니다.");
        }

        // 아이템들 조회
        List<CartItem> items = cartItemRepository.findByCartId(cartId);

        // 옵션 전부 선삭제
        for (CartItem ci : items) {
            cartOptionRepository.deleteByCartItemId(ci.getId());
        }

        // 아이템 전부 삭제
        cartItemRepository.deleteAllInBatch(items);
    }
}
