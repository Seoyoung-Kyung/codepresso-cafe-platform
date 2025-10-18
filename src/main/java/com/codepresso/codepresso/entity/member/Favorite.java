package com.codepresso.codepresso.entity.member;

import com.codepresso.codepresso.entity.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 즐겨찾기 엔티티
 * ERD의 favorite 테이블과 매핑
 */
@Entity
@Table(name = "favorite")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FavoriteId.class)
public class Favorite {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "orderby")
    private Integer orderby;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
}