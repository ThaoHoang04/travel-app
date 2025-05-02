package com.example.travelapp.Domain;

import java.util.Date;

public class Order {
    String orderId;
    String userName;
    String paymentId;
    int itemId;
    String total;
    String date;
    String status;

    public Order() {
    }

    public Order(String orderId, String userName, String paymentId, int itemId, String total, String date, String status) {
        this.orderId = orderId;
        this.userName = userName;
        this.paymentId = paymentId;
        this.itemId = itemId;
        this.total = total;
        this.date = date;
        this.status = status;
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

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
