package com.iyzico.challenge.controller;

import com.iyzico.challenge.map.ProductPurhaceDto;
import com.iyzico.challenge.map.ProductOrderDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAsync
public class PurchasesControllerTest {
    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void should_pay() {
        ProductPurhaceDto productPurhaceDto = new ProductPurhaceDto();
        productPurhaceDto.setBuyerName("Test");
        productPurhaceDto.setBuyerSurname("Test");

        List<ProductOrderDto> productOrderDtoList = new ArrayList<>();
        productOrderDtoList.add(new ProductOrderDto(1, 1));
        productOrderDtoList.add(new ProductOrderDto(2, 2));
        productOrderDtoList.add(new ProductOrderDto(3, 3));
        productOrderDtoList.add(new ProductOrderDto(4, 4));
        productOrderDtoList.add(new ProductOrderDto(5, 5));

        productPurhaceDto.setOrderList(productOrderDtoList);

        HttpEntity<ProductPurhaceDto> entity = new HttpEntity<>(productPurhaceDto, null);

        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    public void should_throwBadRequest_when_overOrderCount() {
        ProductPurhaceDto productPurhaceDto = new ProductPurhaceDto();
        productPurhaceDto.setBuyerName("Test");
        productPurhaceDto.setBuyerSurname("Test");

        List<ProductOrderDto> productOrderDtoList = new ArrayList<>();
        productOrderDtoList.add(new ProductOrderDto(1, 1));
        productOrderDtoList.add(new ProductOrderDto(2, 2));
        productOrderDtoList.add(new ProductOrderDto(3, 3));
        productOrderDtoList.add(new ProductOrderDto(4, 400));

        productPurhaceDto.setOrderList(productOrderDtoList);

        HttpEntity<ProductPurhaceDto> entity = new HttpEntity<>(productPurhaceDto, null);
        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void should_throwBadRequest_with_multipleCustomer_when_overOrderCount() {
        ProductPurhaceDto customer1 = new ProductPurhaceDto();
        customer1.setBuyerName("Test1");
        customer1.setBuyerSurname("Test1");

        List<ProductOrderDto> productOrderDtoList = new ArrayList<>();
        productOrderDtoList.add(new ProductOrderDto(25, 1));


        int stockError = 0;
        for (int i = 0; i < 10; i++) {
            ProductPurhaceDto customer = new ProductPurhaceDto();
            customer.setBuyerName("Test" + i);
            customer.setBuyerSurname("Test" + i);
            customer.setOrderList(productOrderDtoList);
            CompletableFuture<ResponseEntity<Void>> future = purchaseApiCall(customer);
            try {
                ResponseEntity<Void> response = future.get();
                if (response.getStatusCode() == HttpStatus.BAD_REQUEST)
                    stockError++;
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        assertTrue(stockError > 0);
    }

    @Async
    public CompletableFuture<ResponseEntity<Void>> purchaseApiCall(ProductPurhaceDto productPurhaceDto) {
        HttpEntity<ProductPurhaceDto> entity = new HttpEntity<>(productPurhaceDto, null);
        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort(), HttpMethod.POST, entity, Void.class);
        return CompletableFuture.completedFuture(response);
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/purchases";
    }
}
