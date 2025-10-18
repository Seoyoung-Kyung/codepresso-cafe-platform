package com.codepresso.codepresso.entity.order;

import com.codepresso.codepresso.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@Table(name = "order_detail")
@Entity
public class OrdersDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "price")
    private Integer price;

    // 수량 (같은 옵션 조합의 상품이 여러 개 담긴 경우를 표현)
    @Column(name = "quantity")
    private Integer quantity;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "ordersDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdersItemOptions> options;
}
