package com.codepresso.codepresso.product.entity;

import com.codepresso.codepresso.member.entity.Member;
import com.codepresso.codepresso.order.entity.OrdersDetail;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    private BigDecimal rating;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false, unique = true)
    private OrdersDetail ordersDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

}