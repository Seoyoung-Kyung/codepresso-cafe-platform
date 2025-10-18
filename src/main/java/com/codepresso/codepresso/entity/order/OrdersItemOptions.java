package com.codepresso.codepresso.entity.order;

import com.codepresso.codepresso.entity.product.ProductOption;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@Table(name = "order_item_options")
@Entity
public class OrdersItemOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column( name = "order_item_options_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private ProductOption option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrdersDetail ordersDetail;
}
