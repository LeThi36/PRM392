package com.example.project2272.Helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast; // Thêm dòng này
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VNPayHelper {
    
    public static String createPaymentUrl(String orderId, long amount, String orderInfo, String ipAddr, String currencyCode) {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        // Trong method createPaymentUrl
        // Xử lý tiền tệ
        vnp_Params.put("vnp_CurrCode", currencyCode);
        
        // Xử lý amount theo loại tiền tệ
        if ("VND".equals(currencyCode)) {
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VND nhân 100
        } else if ("USD".equals(currencyCode)) {
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // USD cũng nhân 100 (cents)
        }
        vnp_Params.put("vnp_TxnRef", orderId);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", VNPayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddr);
        
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return VNPayConfig.vnp_Url + "?" + queryUrl;
    }
    
    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA512");
            hmacSha512.init(keySpec);
            byte[] result = hmacSha512.doFinal(data.getBytes("UTF-8"));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }
    
    public static boolean openVNPayPayment(Context context, String paymentUrl) {
        try {
            android.util.Log.d("VNPayHelper", "Opening payment URL: " + paymentUrl);
            
            // Kiểm tra URL có hợp lệ không
            if (paymentUrl == null || paymentUrl.isEmpty()) {
                android.util.Log.e("VNPayHelper", "Payment URL is null or empty");
                Toast.makeText(context, "URL thanh toán không hợp lệ!", Toast.LENGTH_LONG).show();
                return false;
            }
            
            // Tạo Intent để mở trình duyệt
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            // Kiểm tra có app nào có thể xử lý không
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                android.util.Log.d("VNPayHelper", "Found app to handle payment URL");
                context.startActivity(intent);
                return true;
            } else {
                android.util.Log.w("VNPayHelper", "No app found to handle URL, trying chooser");
                
                // Thử với Intent chooser
                try {
                    Intent chooser = Intent.createChooser(intent, "Chọn trình duyệt để thanh toán");
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(chooser);
                    return true;
                } catch (Exception chooserEx) {
                    android.util.Log.e("VNPayHelper", "Chooser also failed: " + chooserEx.getMessage());
                    
                    // Thử mở trực tiếp với Chrome nếu có
                    try {
                        Intent chromeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
                        chromeIntent.setPackage("com.android.chrome");
                        chromeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(chromeIntent);
                        return true;
                    } catch (Exception chromeEx) {
                        android.util.Log.e("VNPayHelper", "Chrome not available: " + chromeEx.getMessage());
                    }
                }
            }
            
            Toast.makeText(context, "Không tìm thấy trình duyệt web. Vui lòng cài đặt Chrome hoặc trình duyệt khác.", Toast.LENGTH_LONG).show();
            return false;
            
        } catch (Exception e) {
            android.util.Log.e("VNPayHelper", "Error opening payment URL: " + e.getMessage());
            Toast.makeText(context, "Lỗi mở trang thanh toán: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }
    
    // Thêm method này để test
    public static void testVNPayUrl(Context context) {
        String testUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Version=2.1.0&vnp_Command=pay&vnp_TmnCode=6E5S2EQQ&vnp_Amount=10000&vnp_CurrCode=VND&vnp_TxnRef=TEST123&vnp_OrderInfo=Test&vnp_OrderType=other&vnp_Locale=vn&vnp_ReturnUrl=vnpay://app&vnp_IpAddr=192.168.1.1&vnp_CreateDate=20241225120000&vnp_ExpireDate=20241225121500";
        
        android.util.Log.d("VNPayTest", "Testing with URL: " + testUrl);
        
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(testUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Test URL opened successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            android.util.Log.e("VNPayTest", "Failed to open test URL: " + e.getMessage());
            Toast.makeText(context, "Failed to open test URL: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}