package com.codepresso.codepresso.entity.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "nutrition_info")
@Entity
public class NutritionInfo {

    @Id
    @Column(name = "product_id")
    private Long productId; // PK = FK

    @OneToOne
    @MapsId // PK = FK
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    private double calories;

    private double protein;

    private double fat;

    private double carbohydrate;

    private double saturatedFat;

    @Column(columnDefinition = "DECIMAL(5,1) DEFAULT 0.0")
    private double caffeine;

    private double transFat;

    private double sodium;

    private double sugar;

    private int cholesterol;

}
