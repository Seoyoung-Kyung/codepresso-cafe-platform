package com.codepresso.codepresso.converter.product;

import com.codepresso.codepresso.dto.product.ProductOptionDTO;
import com.codepresso.codepresso.entity.product.OptionName;
import com.codepresso.codepresso.entity.product.OptionStyle;
import com.codepresso.codepresso.entity.product.ProductOption;
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
