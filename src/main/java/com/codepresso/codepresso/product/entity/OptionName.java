package com.codepresso.codepresso.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "option_name")
public class OptionName {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_name_id")
    private Long id;

    @Column(name = "option_name", length = 30)
    private String optionName;

    @OneToMany(mappedBy = "optionName", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OptionStyle> optionStyles = new ArrayList<>();

}