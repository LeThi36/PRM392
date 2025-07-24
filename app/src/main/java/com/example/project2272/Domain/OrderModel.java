package com.example.project2272.Domain;

import java.util.List;
import java.util.Map;

public class OrderModel {
    private String orderId; // Để lưu trữ ID của đơn hàng từ Firebase
    private String userId;
    private String orderDate;
    private double totalAmount;
    private List<Map<String, Object>> items; // Sử dụng List<Map<String, Object>> để phù hợp với cấu trúc JSON

    public OrderModel() {
        // Constructor rỗng cần thiết cho Firebase
    }

    public OrderModel(String userId, String orderDate, double totalAmount, List<Map<String, Object>> items) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.items = items;
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
}