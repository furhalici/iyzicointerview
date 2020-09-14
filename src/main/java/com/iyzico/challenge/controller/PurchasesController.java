package com.iyzico.challenge.controller;

import com.iyzico.challenge.dagger.DaggerIyzicoApiObjectsComponents;
import com.iyzico.challenge.dagger.IyzicoApiObjectsComponents;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.map.ProductPurhaceDto;
import com.iyzico.challenge.map.ProductOrderDto;
import com.iyzico.challenge.service.product.ProductService;
import com.iyzipay.model.Buyer;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "purchases", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchasesController {
    private final ProductService productService;
    private final IyzicoApiObjectsComponents iyzicoApiObjectsComponents;

    public PurchasesController(ProductService productService) {
        this.productService = productService;
        iyzicoApiObjectsComponents = DaggerIyzicoApiObjectsComponents.create();
    }

    @PostMapping
    public ResponseEntity<Void> buy(@RequestBody ProductPurhaceDto productPurhaceDto) {
        List<Product> productList = productPurhaceDto.getOrderList().stream().map(this::convertProduct).collect(Collectors.toList());
        Buyer buyer = iyzicoApiObjectsComponents.provideBuyer();
        buyer.setName(productPurhaceDto.getBuyerName());
        buyer.setName(productPurhaceDto.getBuyerSurname());
        productService.buy(productList, buyer);
        return ResponseEntity.noContent().build();
    }

    private Product convertProduct(ProductOrderDto productOrderDto) {
        Product product = productService.getProductById(productOrderDto.getId());
        product.setOrderCount(productOrderDto.getOrderCount());
        return product;
    }
}
