package com.iyzico.challenge.service;

import com.iyzico.challenge.dagger.DaggerIyzicoApiObjectsComponents;
import com.iyzico.challenge.dagger.IyzicoApiObjectsComponents;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.ProductStockException;
import com.iyzico.challenge.exception.ResourceNotFoundException;
import com.iyzico.challenge.repository.ProductRepository;
import com.iyzico.challenge.service.payment.IyzicoApiPaymentService;
import com.iyzico.challenge.service.product.ProductService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);

    @Mock
    private ProductRepository productRepository;

    @Mock
    private IyzicoApiPaymentService iyzicoApiPaymentService;

    @InjectMocks
    private ProductService productService;

    private static IyzicoApiObjectsComponents iyzicoApiObjectsComponents;

    @BeforeAll
    public static void init() {
        iyzicoApiObjectsComponents = DaggerIyzicoApiObjectsComponents.create();
    }


    @Test
    public void should_get_with_id() {
        final Product product = createProduct(1L);
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        assertThat(productService.getProductById(1L)).isNotNull();
    }

    @Test
    public void should_throw_ResourceNotFoundException_when_idNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    public void should_list() {
        List<Product> list = new ArrayList<>();
        list.add(createProduct(1L));
        list.add(createProduct(2L));
        list.add(createProduct(3L));
        list.add(createProduct(4L));

        given(productRepository.findAll()).willReturn(list);

        assertEquals(list, productService.getAllProducts());
    }

    @Test
    public void should_delete_with_id() {
        long id = 1;
        productService.deleteProduct(id);
        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    public void should_decrease_with_id() {
        Product product = createProduct(1L);
        product.setOrderCount(2);
        productService.decreaseStock(product);
        assertEquals(3, product.getStockCount());
    }

    @Test
    public void should_productStockException_when_orderCountBiggerThanStockCount() {
        Product product = createProduct(1L);
        product.setOrderCount(25);
        assertThrows(ProductStockException.class, () -> productService.decreaseStock(product));
    }

    @Test
    public void should_returnFalse_when_notEnoughStockCount() {
        Product product = createProduct(1L);
        product.setOrderCount(25);
        assertFalse(productService.availableStock(product));
    }

    @Test
    public void should_returnFalse_when_enoughStockCount() {
        Product product = createProduct(1L);
        product.setOrderCount(5);
        assertTrue(productService.availableStock(product));
    }

    @Test
    public void should_calculate_with_productId() {
        Product product = createProduct(1L);
        // 55.35 * 3
        assertEquals(new BigDecimal("166.05"), productService.calculateProductTotalPrice(product));
    }

    @Test
    public void should_calculate_with_productId_when_priceIsZero() {
        Product product = createProduct(1L);
        product.setPrice(new BigDecimal(0));
        // 55.35 * 0
        assertEquals(BigDecimal.ZERO, productService.calculateProductTotalPrice(product));
    }

    @Test
    public void should_totalPrice_with_zero() {
        Product product = createProduct(1L);
        product.setOrderCount(0);
        // 55.35 * 0
        assertEquals(BigDecimal.ZERO, productService.calculateProductTotalPrice(product));
    }

    @Test
    public void should_totalPrice() {
        List<Product> list = new ArrayList<>();
        Product product = createProduct(1L);
        list.add(product);
        Product product2 = createProduct(2L);
        product2.setPrice(new BigDecimal("2.2222"));
        product2.setOrderCount(6);
        list.add(product2);
        Product product3 = createProduct(3L);
        product3.setPrice(new BigDecimal("13.311"));
        product3.setOrderCount(4);
        list.add(product3);


        // 55.35 * 3 +  2.2222 * 6 + 13.311 * 4
        assertEquals(new BigDecimal("232.6272"), productService.calculateProductTotalPrice(list));
    }

    @Test
    public void should_buy_with_multipleAvailableProduct() {
        List<Product> list = new ArrayList<>();
        Product product = createProduct(1L);
        product.setOrderCount(5);
        list.add(product);
        Product product2 = createProduct(2L);
        product2.setPrice(new BigDecimal("2.2222"));
        product2.setOrderCount(5);
        list.add(product2);
        Product product3 = createProduct(3L);
        product3.setPrice(new BigDecimal("13.311"));
        product3.setOrderCount(1);
        list.add(product3);

        assertDoesNotThrow(() -> productService.buy(list, iyzicoApiObjectsComponents.provideBuyer()));
    }

    @Test
    public void should_true_when_unavailableStock() {
        List<Product> list = new ArrayList<>();
        Product product = createProduct(1L);
        product.setOrderCount(5);
        list.add(product);
        Product product2 = createProduct(2L);
        product2.setPrice(new BigDecimal("2.2222"));
        product2.setOrderCount(5);
        list.add(product2);
        Product product3 = createProduct(3L);
        product3.setPrice(new BigDecimal("13.311"));
        product3.setOrderCount(1);
        list.add(product3);

        assertTrue(productService.checkProductsStock(list));
    }


    @Test
    public void should_false_when_unavailableStock() {
        List<Product> list = new ArrayList<>();
        Product product = createProduct(1L);
        product.setOrderCount(100);
        list.add(product);
        Product product2 = createProduct(2L);
        product2.setPrice(new BigDecimal("2.2222"));
        product2.setOrderCount(555);
        list.add(product2);
        Product product3 = createProduct(3L);
        product3.setPrice(new BigDecimal("13.311"));
        product3.setOrderCount(111);
        list.add(product3);

        assertFalse(productService.checkProductsStock(list));
    }

    @Test
    public void should_throwProductStockExceptionWhenOverOrderCount() {
        List<Product> list = new ArrayList<>();
        Product product = createProduct(1L);
        product.setOrderCount(5);
        list.add(product);
        Product product2 = createProduct(2L);
        product2.setPrice(new BigDecimal("2.2222"));
        product2.setOrderCount(5);
        list.add(product2);
        Product product3 = createProduct(3L);
        product3.setPrice(new BigDecimal("13.311"));
        product3.setOrderCount(100);
        list.add(product3);

        assertThrows(ProductStockException.class, () -> productService.buy(list, iyzicoApiObjectsComponents.provideBuyer()));
    }


    private Product createProduct(long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Ürün");
        product.setDescription("Açıklama");
        product.setStockCount(5);
        product.setPrice(new BigDecimal("55.35"));
        product.setOrderCount(3);
        return product;
    }
}
