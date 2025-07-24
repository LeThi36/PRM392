package com.example.project2272.Helper;

public class VNPayConfig {
    // Thông tin cấu hình VNPay Sandbox
    public static final String vnp_TmnCode = "6E5S2EQQ";
    public static final String vnp_HashSecret = "DO1Y00OCUFWD69HA8HVWUDI5L8JJLBFH";
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String vnp_ReturnUrl = "vnpay://app";
    
    // Thêm các biến còn thiếu
    public static final String vnp_Version = "2.1.0";
    public static final String vnp_Command = "pay";
    public static final String vnp_OrderType = "other";
    
    // Thêm cấu hình tiền tệ
    public static final String vnp_CurrCode_VND = "VND";
    public static final String vnp_CurrCode_USD = "USD";
}