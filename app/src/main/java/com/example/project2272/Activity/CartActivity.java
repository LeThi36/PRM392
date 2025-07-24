package com.example.project2272.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project2272.Adapter.CartAdapter;
import com.example.project2272.Domain.ItemsModel;
import com.example.project2272.Domain.OrderModel;
import com.example.project2272.Helper.ManagmentCart;
import com.example.project2272.Helper.VNPayHelper;
import com.example.project2272.Manager.AuthManager;
import com.example.project2272.R;
import com.example.project2272.databinding.ActivityCartBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;
    private DatabaseReference mDatabase;
    private double totalAmount;
    private boolean isCODSelected = true;
    private String currentVNPayOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        calculatorCart();
        setVariable();
        initCartList();

        // THÊM: Xử lý VNPay callback khi app được mở từ VNPay
        handleVNPayReturn(getIntent());
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView2.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView2.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), this, () -> calculatorCart()));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        // Xử lý thay đổi phương thức thanh toán
        binding.paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.codRadioBtn) {
                isCODSelected = true;
                binding.checkoutBtn.setText("Đặt hàng COD");
                binding.checkoutBtn.setBackgroundResource(R.drawable.orange_bg);
            } else if (checkedId == R.id.vnpayRadioBtn) {
                isCODSelected = false;
                binding.checkoutBtn.setText("Thanh toán VNPay");
                binding.checkoutBtn.setBackgroundResource(R.drawable.orange_bg);
            }
        });

        // Xử lý checkout
        binding.checkoutBtn.setOnClickListener(v -> {
            if (managmentCart.getListCart().isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isCODSelected) {
                processCODOrder();
            } else {
                processVNPayPayment();
            }
        });
    }

    private void processCODOrder() {
        String userId = AuthManager.getCurrentUser() != null ? AuthManager.getCurrentUser().getUserId() : null;

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo mã đơn hàng COD
        String orderId = "COD_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

        // Hiển thị dialog xác nhận
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận đơn hàng COD")
                .setMessage("Mã đơn hàng: " + orderId + "\n" +
                        "Tổng tiền: đ" + String.format("%.0f", totalAmount) + "\n" +
                        "Bạn sẽ thanh toán khi nhận hàng.\n" +
                        "Xác nhận đặt hàng?")
                .setPositiveButton("Xác nhận", (dialog, which) -> {
                    saveCODOrderToFirebase(orderId, userId);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveCODOrderToFirebase(String orderId, String userId) {
        // Chuyển đổi cart items thành format phù hợp
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (ItemsModel item : managmentCart.getListCart()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("title", item.getTitle());
            itemMap.put("price", item.getPrice());
            itemMap.put("numberInCart", item.getNumberInCart());
            itemMap.put("picUrl", item.getPicUrl().get(0)); // Lấy ảnh đầu tiên
            orderItems.add(itemMap);
        }

        // Tạo OrderModel với trạng thái "Chưa thanh toán" cho COD
        String orderDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        OrderModel order = new OrderModel(userId, orderDate, totalAmount, orderItems, "COD", "Chưa thanh toán"); // ĐÃ SỬA

        // Lưu vào Firebase
        mDatabase.child("Orders").child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đặt hàng COD thành công!", Toast.LENGTH_LONG).show();

                    // Xóa giỏ hàng
                    managmentCart.getListCart().clear();

                    // Chuyển đến OrderHistoryActivity
                    Intent intent = new Intent(CartActivity.this, OrderHistoryActivity.class);
                    intent.putExtra("newOrderId", orderId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi đặt hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void processVNPayPayment() {
        String userId = AuthManager.getCurrentUser() != null ? AuthManager.getCurrentUser().getUserId() : null;

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thanh toán.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Tạo mã đơn hàng VNPay
            String orderId = "VNPAY_" + new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            this.currentVNPayOrderId = orderId;

            String orderInfo = "Thanh toan don hang " + orderId;
            String ipAddr = "192.168.1.1";

            // Logging chi tiết
            android.util.Log.d("VNPay", "=== VNPay Payment Process Started ===");
            android.util.Log.d("VNPay", "Order ID: " + orderId);
            android.util.Log.d("VNPay", "Total amount: " + totalAmount);

            // Tạo URL thanh toán
            String paymentUrl = VNPayHelper.createPaymentUrl(orderId, (long) totalAmount, orderInfo, ipAddr, "VND");

            android.util.Log.d("VNPay", "Generated payment URL: " + paymentUrl);

            // Kiểm tra URL có được tạo thành công không
            if (paymentUrl == null || paymentUrl.isEmpty()) {
                android.util.Log.e("VNPay", "Failed to create payment URL");
                Toast.makeText(this, "Lỗi tạo URL thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra URL có chứa các thông tin cần thiết không
            if (!paymentUrl.contains("vnp_Amount") || !paymentUrl.contains("vnp_TxnRef")) {
                android.util.Log.e("VNPay", "Payment URL missing required parameters");
                Toast.makeText(this, "URL thanh toán không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Thử mở trang thanh toán
            android.util.Log.d("VNPay", "Attempting to open payment page...");
            boolean success = VNPayHelper.openVNPayPayment(this, paymentUrl);

            if (success) {
                android.util.Log.d("VNPay", "Successfully opened payment page");
                Toast.makeText(this, "Đang chuyển đến trang thanh toán VNPay...", Toast.LENGTH_SHORT).show();
            } else {
                android.util.Log.e("VNPay", "Failed to open payment page");
                Toast.makeText(this, "Không thể mở trang thanh toán VNPay! Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                return;
            }

        } catch (Exception e) {
            android.util.Log.e("VNPay", "Exception in processVNPayPayment: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi xử lý thanh toán: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10.0;
        tax = Math.round((managmentCart.getTotalFee() * percentTax * 100)) / 100.0;

        totalAmount = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100.0;
        double itemTotal = Math.round((managmentCart.getTotalFee() * 100)) / 100.0;

        binding.totalFeeTxt.setText(itemTotal + " " + "VND");
        binding.taxTxt.setText(tax + " " + "VND");
        binding.deliveryTxt.setText(delivery + " " + "VND");
        binding.totalTxt.setText(totalAmount + " " + "VND");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleVNPayReturn(intent);
    }

    private void handleVNPayReturn(Intent intent) {
        Uri data = intent.getData();
        if (data != null && "vnpay".equals(data.getScheme())) {
            String responseCode = data.getQueryParameter("vnp_ResponseCode");
            String transactionStatus = data.getQueryParameter("vnp_TransactionStatus");
            String orderId = data.getQueryParameter("vnp_TxnRef");

            android.util.Log.d("VNPay", "=== VNPay Callback Received ===");
            android.util.Log.d("VNPay", "Response Code: " + responseCode);
            android.util.Log.d("VNPay", "Transaction Status: " + transactionStatus);
            android.util.Log.d("VNPay", "Order ID: " + orderId);

            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                // Thanh toán VNPay thành công
                saveVNPayOrderToFirebase(orderId);
            } else {
                // Thanh toán thất bại
                String errorMsg = "Thanh toán thất bại!";
                if ("24".equals(responseCode)) {
                    errorMsg = "Giao dịch bị hủy bởi người dùng";
                } else if ("11".equals(responseCode)) {
                    errorMsg = "Giao dịch đã hết hạn";
                } else if ("07".equals(responseCode)) {
                    errorMsg = "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường)";
                } else if ("09".equals(responseCode)) {
                    errorMsg = "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng";
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                android.util.Log.e("VNPay", "Payment failed: " + errorMsg);
            }
        }
    }

    private void saveVNPayOrderToFirebase(String orderId) {
        String userId = AuthManager.getCurrentUser().getUserId();

        // Chuyển đổi cart items
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (ItemsModel item : managmentCart.getListCart()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("title", item.getTitle());
            itemMap.put("price", item.getPrice());
            itemMap.put("numberInCart", item.getNumberInCart());
            itemMap.put("picUrl", item.getPicUrl().get(0));
            orderItems.add(itemMap);
        }

        String orderDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        OrderModel order = new OrderModel(userId, orderDate, totalAmount, orderItems, "VNPay", "Đã thanh toán"); // ĐÃ SỬA

        android.util.Log.d("VNPay", "Saving order to Firebase: " + orderId);

        mDatabase.child("Orders").child(orderId).setValue(order)
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("VNPay", "Order saved successfully");
                    Toast.makeText(this, "Thanh toán VNPay thành công!", Toast.LENGTH_LONG).show();

                    // Xóa giỏ hàng
                    managmentCart.getListCart().clear();

                    // Chuyển đến OrderHistoryActivity
                    Intent intent = new Intent(CartActivity.this, OrderHistoryActivity.class);
                    intent.putExtra("newOrderId", orderId);
                    startActivity(intent);
                    finish();
                });
    }
}