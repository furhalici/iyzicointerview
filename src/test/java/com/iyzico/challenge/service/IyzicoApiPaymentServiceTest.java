package com.iyzico.challenge.service;

import com.iyzico.challenge.dagger.DaggerIyzicoApiObjectsComponents;
import com.iyzico.challenge.dagger.IyzicoApiObjectsComponents;
import com.iyzico.challenge.exception.IyzicoPaymentException;
import com.iyzico.challenge.service.payment.IyzicoApiPaymentService;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.request.CreatePaymentRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IyzicoApiPaymentServiceTest {

    @Autowired
    private IyzicoApiPaymentService iyzicoApiPaymentService;

    private static IyzicoApiObjectsComponents iyzicoApiObjectsComponents;

    @BeforeAll
    public static void init() {
        iyzicoApiObjectsComponents = DaggerIyzicoApiObjectsComponents.create();
    }

    @Test
    public void should_pay() {
        CreatePaymentRequest createPaymentRequest = iyzicoApiObjectsComponents.provideCreatePaymentRequest();
        createPaymentRequest.setPrice(new BigDecimal("30"));
        createPaymentRequest.setPaidPrice(new BigDecimal("30"));
        List<BasketItem> basketItemList = Stream.of(createBasketItem(),createBasketItem(), createBasketItem()).collect(Collectors.toList());
        createPaymentRequest.setBasketItems(basketItemList);
        createPaymentRequest.setBuyer(iyzicoApiObjectsComponents.provideBuyer());

        assertDoesNotThrow(() -> iyzicoApiPaymentService.pay(createPaymentRequest));
    }

    @Test
    public void should_throwIyzicoPaymentException_when_priceIsZero() {
        CreatePaymentRequest createPaymentRequest = iyzicoApiObjectsComponents.provideCreatePaymentRequest();
        createPaymentRequest.setPrice(BigDecimal.ZERO);
        createPaymentRequest.setPrice(BigDecimal.ZERO);
        List<BasketItem> basketItemList = Stream.of(createBasketItem(),createBasketItem(), createBasketItem()).collect(Collectors.toList());
        createPaymentRequest.setBasketItems(basketItemList);
        createPaymentRequest.setBuyer(iyzicoApiObjectsComponents.provideBuyer());

        assertThrows(IyzicoPaymentException.class, () -> iyzicoApiPaymentService.pay(createPaymentRequest));
    }

    private BasketItem createBasketItem() {
        BasketItem basketItem = new BasketItem();
        basketItem.setId("123");
        basketItem.setName("Ürün");
        basketItem.setCategory1("Game");
        basketItem.setCategory2("Online Game Items");
        basketItem.setItemType(BasketItemType.VIRTUAL.name());
        basketItem.setPrice(new BigDecimal("10"));
        return basketItem;
    }
}
