package com.iyzico.challenge.map;

import java.util.List;

public class ProductPurhaceDto {
    private List<ProductOrderDto> orderList;
    private String buyerName;
    private String buyerSurname;

    public List<ProductOrderDto> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<ProductOrderDto> orderList) {
        this.orderList = orderList;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerSurname() {
        return buyerSurname;
    }

    public void setBuyerSurname(String buyerSurname) {
        this.buyerSurname = buyerSurname;
    }
}
