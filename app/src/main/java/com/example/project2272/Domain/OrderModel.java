package com.example.project2272.Domain;

import java.util.List;
import java.util.Map;

public class OrderModel {
    private String orderId;
    private String userId;
    private String orderDate;
    private double totalAmount;
    private List<Map<String, Object>> items;
    private String paymentMethod; // Thêm trường này
    private String orderStatus; // Thêm trường này

    public OrderModel() {
        // Constructor rỗng cần thiết cho Firebase
    }

    public OrderModel(String userId, String orderDate, double totalAmount, List<Map<String, Object>> items, String paymentMethod, String orderStatus) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.orderStatus = orderStatus;
    }

    // --- Getters và Setters ---
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}