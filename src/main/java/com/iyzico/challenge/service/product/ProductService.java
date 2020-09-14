package com.iyzico.challenge.service.product;

import com.iyzico.challenge.dagger.DaggerIyzicoApiObjectsComponents;
import com.iyzico.challenge.dagger.IyzicoApiObjectsComponents;
import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.exception.ProductStockException;
import com.iyzico.challenge.exception.ResourceNotFoundException;
import com.iyzico.challenge.repository.ProductRepository;
import com.iyzico.challenge.service.payment.IyzicoApiPaymentService;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.request.CreatePaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final IyzicoApiObjectsComponents iyzicoApiObjectsComponents;

    private final ProductRepository productRepository;
    private final IyzicoApiPaymentService iyzicoApiPaymentService;

    public ProductService(ProductRepository productRepository, IyzicoApiPaymentService iyzicoApiPaymentService) {
        this.productRepository = productRepository;
        this.iyzicoApiPaymentService = iyzicoApiPaymentService;
        iyzicoApiObjectsComponents = DaggerIyzicoApiObjectsComponents.create();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product saveOrUpdateProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    public void deleteProduct(long id) {
        productRepository.deleteById(id);
    }

    public void decreaseStock(Product product) {
        int count = product.getStockCount() - product.getOrderCount();
        if (count < 0)
            throw new ProductStockException();
        product.setStockCount(count);
        productRepository.save(product);
    }

    public boolean availableStock(Product product) {
        return product.getStockCount() >= product.getOrderCount() && product.getOrderCount() > 0;
    }

    public boolean checkProductsStock(List<Product> products) {
        return products.stream().allMatch(this::availableStock);
    }

    public void buy(List<Product> products, Buyer buyer) {
        if (checkProductsStock(products)) {
            BigDecimal totalPrice = calculateProductTotalPrice(products);
            CreatePaymentRequest createPaymentRequest = iyzicoApiObjectsComponents.provideCreatePaymentRequest();
            createPaymentRequest.setPrice(totalPrice);
            createPaymentRequest.setPaidPrice(totalPrice);

            List<BasketItem> basketItemList = products.stream().map(this::convertToBasketItem).collect(Collectors.toList());
            createPaymentRequest.setBasketItems(basketItemList);

            createPaymentRequest.setBuyer(buyer);

            iyzicoApiPaymentService.pay(createPaymentRequest);

            products.forEach(this::decreaseStock);
        }
        else {
            final String unavailableProducts = products.stream()
                    .filter(product -> !availableStock(product))
                    .map(Product::getName)
                    .collect(Collectors.joining(","));
            logger.error("Unavailable Stock in {}", unavailableProducts);
            throw new ProductStockException(unavailableProducts);
        }
    }

    private BasketItem convertToBasketItem(Product product) {
        BasketItem basketItem = new BasketItem();
        basketItem.setId("P:" + product.getId());
        basketItem.setName(product.getName());
        basketItem.setCategory1("Game");
        basketItem.setCategory2("Online Game Items");
        basketItem.setItemType(BasketItemType.VIRTUAL.name());
        basketItem.setPrice(calculateProductTotalPrice(product));
        return basketItem;
    }

    public BigDecimal calculateProductTotalPrice(List<Product> list) {
        return list.stream()
                .map(this::calculateProductTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateProductTotalPrice(Product product) {
        if (product.getPrice().compareTo(BigDecimal.ZERO) == 0 || product.getOrderCount() == 0) return BigDecimal.ZERO;
        return product.getPrice().multiply(new BigDecimal(product.getOrderCount()));
    }
}
