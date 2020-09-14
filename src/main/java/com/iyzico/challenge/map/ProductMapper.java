package com.iyzico.challenge.map;

import com.iyzico.challenge.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface ProductMapper {

    @Mapping(target = "orderCount", ignore = true)
    Product dtoToDomain(ProductDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderCount", ignore = true)
    Product dtoToDomain(ProductCreateDto createUserDto);

    ProductDto domainToDto(Product user);

}
