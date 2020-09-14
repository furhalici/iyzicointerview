package com.iyzico.challenge.map;

public class ProductOrderDto {
    private long id;
    private int orderCount;

    public ProductOrderDto() {
    }

    public ProductOrderDto(long id, int orderCount) {
        this.id = id;
        this.orderCount = orderCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
