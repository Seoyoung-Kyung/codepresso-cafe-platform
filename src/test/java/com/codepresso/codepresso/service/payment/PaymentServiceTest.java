package com.codepresso.codepresso.service.payment;

import com.codepresso.codepresso.dto.payment.CheckoutRequest;
import com.codepresso.codepresso.entity.branch.Branch;
import com.codepresso.codepresso.entity.member.Member;
import com.codepresso.codepresso.entity.product.Product;
import com.codepresso.codepresso.entity.product.ProductOption;
import com.codepresso.codepresso.repository.branch.BranchRepository;
import com.codepresso.codepresso.repository.member.MemberRepository;
import com.codepresso.codepresso.repository.order.OrdersRepository;
import com.codepresso.codepresso.repository.product.ProductRepository;
import com.codepresso.codepresso.service.cart.CartService;
import com.codepresso.codepresso.service.product.ProductService;
import org.hibernate.annotations.Check;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BranchRepository branchRepository;
    @Mock
    private ProductService productService;
    @Mock
    private CartService cartService;

    private Member testmember;
    private Branch testbranch;
    private Product testproduct;
    private ProductOption testproductoption;
    private CheckoutRequest testRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 데이터
        testmember = Member.builder()
                .id(1L)
                .email("test@test.com")
                .name("테스트 유저")
                .build();

        // 테스트용 지점 데이터
        testbranch = Branch.builder().id(1L).build();

        // 테스트용 프로덕트 데이터
        testproduct = Product.builder()
                .id(1L)
                .productName("아메리카노")
                .price(4500)
                .build();

        testproductoption = ProductOption.builder()
                .id(1L)
                .build();

        testRequest = new CheckoutRequest();
        testRequest.setMemberId(1L);
        testRequest.setBranchId(1L);
        testRequest.setIsTakeout(true);
        testRequest.setPickupTime(LocalDateTime.now().plusHours(1));
    }


}