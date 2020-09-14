package com.iyzico.challenge.exception;

public class ProductStockException extends RuntimeException{
    public ProductStockException() {
        super("Unavailable Stock");
    }
    public ProductStockException(String products) {
        super("Unavailable Stock in " + products);
    }
}
