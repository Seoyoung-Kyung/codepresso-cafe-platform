package com.codepresso.codepresso.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "allergen")
@Entity
public class Allergen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allergen_id")
    private Long id;

    @Column(name = "allergen_name", nullable = false)
    private String allergenName;

    @Column(name = "allergen_code")
    private String allergenCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;
}
