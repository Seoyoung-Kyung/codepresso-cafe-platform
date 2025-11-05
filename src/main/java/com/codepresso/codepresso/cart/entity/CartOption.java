package com.codepresso.codepresso.cart.entity;

import com.codepresso.codepresso.product.entity.ProductOption;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="cart_option")
@Entity
public class CartOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="cart_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_item_id", nullable = false)
    private CartItem cartItem;
}
