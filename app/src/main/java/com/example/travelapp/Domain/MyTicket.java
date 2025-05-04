package com.example.travelapp.Domain;

import com.example.travelapp.Activity.HelperClass;

public class MyTicket {
    String orderId;
    String userName;
    String paymentId;
    int itemId;
    String total;
    String date;
    String status;
    HelperClass userInfo;
    ItemDomain tourInfo;

    public MyTicket(String orderId, String userName, String paymentId, int itemId, String total, String date, String status, HelperClass userInfo, ItemDomain tourInfo) {
        this.orderId = orderId;
        this.userName = userName;
        this.paymentId = paymentId;
        this.itemId = itemId;
        this.total = total;
        this.date = date;
        this.status = status;
        this.userInfo = userInfo;
        this.tourInfo = tourInfo;
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

    public HelperClass getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(HelperClass userInfo) {
        this.userInfo = userInfo;
    }

    public ItemDomain getTourInfo() {
        return tourInfo;
    }

    public void setTourInfo(ItemDomain tourInfo) {
        this.tourInfo = tourInfo;
    }
}
