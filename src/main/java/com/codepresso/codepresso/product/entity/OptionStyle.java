package com.codepresso.codepresso.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "option_style")
@Entity
public class OptionStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_style_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_name_id", nullable = false)
    private OptionName optionName;

    @Column(name = "option_style", length = 100)
    private String optionStyle;

    @Column(name = "extra_price")
    private int extraPrice;

    @OneToMany(mappedBy = "optionStyle", cascade = CascadeType.ALL)
    List<ProductOption> productOptions;
}
