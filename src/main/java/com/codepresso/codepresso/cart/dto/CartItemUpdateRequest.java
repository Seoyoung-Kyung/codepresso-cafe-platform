package com.codepresso.codepresso.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateRequest {

    @NotNull
    @Min(1)
    private Integer quantity;

    private List<Long> optionIds;
}
