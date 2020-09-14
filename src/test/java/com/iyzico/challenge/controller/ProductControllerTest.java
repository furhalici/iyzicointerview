package com.iyzico.challenge.controller;

import com.iyzico.challenge.map.ProductCreateDto;
import com.iyzico.challenge.map.ProductDto;
import com.iyzico.challenge.map.ProductMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    ProductMapper productMapper;

    String TAG = "/products";

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @Test
    public void should_list() {
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ParameterizedTypeReference<List<ProductDto>> parameterizedTypeReference = new ParameterizedTypeReference<List<ProductDto>>() {
        };

        ResponseEntity<List<ProductDto>> response = restTemplate.exchange(createURLWithPort("/"), HttpMethod.GET, entity, parameterizedTypeReference);

        List<ProductDto> list = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotEquals(0, list.size());
    }

    @Test
    public void should_create_with_productObject() {
        ProductCreateDto excepted = new ProductCreateDto();
        excepted.setName("name");
        excepted.setPrice(new BigDecimal("15.0"));
        excepted.setStockCount(15);
        excepted.setDescription("desc");

        HttpEntity<ProductCreateDto> entity = new HttpEntity<>(excepted, headers);

        ResponseEntity<ProductDto> response = restTemplate.exchange(createURLWithPort(""), HttpMethod.POST, entity, ProductDto.class);

        ProductDto actual = response.getBody();

        assertNotNull(actual.getId());
        assertEquals(excepted.getName(), actual.getName());
        assertEquals(excepted.getPrice(), actual.getPrice());
        assertEquals(excepted.getDescription(), actual.getDescription());
        assertEquals(excepted.getStockCount(), actual.getStockCount());
    }

    @Test
    public void should_delete_with_id() {
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort("/15"), HttpMethod.DELETE, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    public void should_get_with_id() {
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<ProductDto> response = restTemplate.exchange(createURLWithPort("/1"), HttpMethod.GET, entity, ProductDto.class);

        ProductDto actual = response.getBody();

        assertEquals(1L, actual.getId().longValue());
        assertEquals("Ürün1", actual.getName());
        assertEquals(new BigDecimal("15.25"), actual.getPrice());
        assertEquals("Ürün1", actual.getDescription());
        assertEquals(10, actual.getStockCount());

    }

    @Test
    public void should_update() {
        ProductDto excepted = new ProductDto();
        excepted.setId(5L);
        excepted.setName("new name");
        excepted.setPrice(new BigDecimal("10.0"));
        excepted.setStockCount(5);
        excepted.setDescription("new desc");

        HttpEntity<ProductDto> entity = new HttpEntity<>(excepted, headers);

        ResponseEntity<ProductDto> response = restTemplate.exchange(createURLWithPort("/"), HttpMethod.PUT, entity, ProductDto.class);

        ProductDto actual = response.getBody();

        assertEquals(excepted.getId(), actual.getId());
        assertEquals(excepted.getName(), actual.getName());
        assertEquals(excepted.getPrice(), actual.getPrice());
        assertEquals(excepted.getDescription(), actual.getDescription());
        assertEquals(excepted.getStockCount(), actual.getStockCount());

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + TAG + uri;
    }
}
