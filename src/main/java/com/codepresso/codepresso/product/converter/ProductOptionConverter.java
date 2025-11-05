package com.codepresso.codepresso.product.converter;

import com.codepresso.codepresso.product.dto.ProductOptionDTO;
import com.codepresso.codepresso.product.entity.OptionName;
import com.codepresso.codepresso.product.entity.OptionStyle;
import com.codepresso.codepresso.product.entity.ProductOption;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductOptionConverter {

    public ProductOptionDTO toDto(ProductOption productOption) {
        OptionStyle optionStyle = productOption.getOptionStyle();
        OptionName optionName = optionStyle.getOptionName();

        return ProductOptionDTO.builder()
                .optionStyleId(optionStyle.getId())
                .optionId(productOption.getId())
                .optionName(optionName.getOptionName())
                .optionStyleName(optionStyle.getOptionStyle())
                .extraPrice(optionStyle.getExtraPrice())
                .build();
    }

    public List<ProductOptionDTO> toListDto(List<ProductOption> productOptions) {
        return productOptions.stream()
                .map(option -> this.toDto(option))
                .toList();
    }

}
