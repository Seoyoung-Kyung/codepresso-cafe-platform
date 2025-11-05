package com.codepresso.codepresso.entity.product;
import com.codepresso.codepresso.entity.member.Favorite;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_content")
    private String productContent;

    @Column(name = "product_photo")
    private String productPhoto;

    private Integer price;

    @Column(name = "favorite_count", nullable = false, columnDefinition = "bigint default 0")
    @Builder.Default
    private Long favoriteCount = 0L;

    @Version // 낙관적 락 테스트
    @Column(name = "version")
    private Long version;

    // 1:N
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    // 1:1
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private NutritionInfo nutritionInfo;

    // 1:N
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Allergen> allergens = new HashSet<>();

    // 1:N - Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // 1:N 관계 매핑 (즐겨찾기만)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favorite> favorites = new ArrayList<>();

    // 1:N - Hashtag
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Hashtag> hashtags = new HashSet<>();

    public void increaseFavoriteCount() {
        if(this.favoriteCount == null) {
            this.favoriteCount = 1L;
        } else {
            this.favoriteCount++;
        }
    }

    public void decreaseFavoriteCount() {
        if(favoriteCount > 0) {
            this.favoriteCount--;
        }
    }
}
