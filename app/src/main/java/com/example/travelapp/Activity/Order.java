package com.example.travelapp.Activity;

public class Order {
    String orderId;
    String userName;
    String paymentId;
    String itemId;
    String total;

    public Order() {
    }

    public Order(String orderId, String userName, String paymentId, String itemId, String total) {
        this.orderId = orderId;
        this.userName = userName;
        this.paymentId = paymentId;
        this.itemId = itemId;
        this.total = total;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
