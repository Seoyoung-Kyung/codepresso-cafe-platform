package com.codepresso.codepresso.repository.cart;

import com.codepresso.codepresso.cart.repository.CartRepository;
import com.codepresso.codepresso.cart.entity.Cart;
import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test") // 이거 꼭 적어야함
@DataJpaTest
class CartRepositoryTest
{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void saveTest() {
        Member member = Member.builder()
                .accountId("test")
                .password("1234")
                .nickname("testnickname")
                .role(Member.Role.USER)
                .build();
        Member savedMember = memberRepository.saveAndFlush(member);

        Cart cart = Cart.builder()
                .member(savedMember)
                .build();
        Cart savedCart = cartRepository.saveAndFlush(cart);

        Cart foundCart = cartRepository.findById(savedCart.getId())
                .orElseThrow(() -> new AssertionError("장바구니가 저장되지 않았습니다."));
        assertThat(foundCart.getMember().getAccountId()).isEqualTo("test");
    }
}