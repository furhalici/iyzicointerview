package com.iyzico.challenge.controller;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.map.ProductCreateDto;
import com.iyzico.challenge.map.ProductDto;
import com.iyzico.challenge.map.ProductMapper;
import com.iyzico.challenge.service.product.ProductService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "products", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService, ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts().stream().map(productMapper::domainToDto).collect(Collectors.toList()));
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable long id) {
        return ResponseEntity.ok(productMapper.domainToDto(productService.getProductById(id)));
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductCreateDto productDto) {

        Product product = productService.saveOrUpdateProduct(productMapper.dtoToDomain(productDto));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        return ResponseEntity.created(location).body(productMapper.domainToDto(product));
    }

    @PutMapping
    public ResponseEntity<ProductDto> update(@RequestBody ProductDto productDto) {
        Product product = productMapper.dtoToDomain(productDto);
        return ResponseEntity.ok(productMapper.domainToDto(productService.saveOrUpdateProduct(product)));
    }

    @DeleteMapping(value = "{id}", consumes = {})
    public ResponseEntity<Void> delete(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
