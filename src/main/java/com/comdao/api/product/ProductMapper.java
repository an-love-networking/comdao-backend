package com.comdao.api.product;

import com.comdao.api.product.dto.ProductCreationDto;
import com.comdao.api.product.dto.ProductDto;
import com.comdao.api.product.dto.ProductUpdateDto;
import com.comdao.api.product.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    public ProductDto convertToProductDto(Product product);

    public Product convertToProduct(ProductCreationDto dto);

    public Product convertToProduct(ProductUpdateDto dto);

    public void updateProduct(ProductUpdateDto update, @MappingTarget Product product);
}
