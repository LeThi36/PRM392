package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project2272.Adapter.CartAdapter;
import com.example.project2272.Domain.ItemsModel;
import com.example.project2272.Helper.ManagmentCart;
import com.example.project2272.Manager.AuthManager;
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

        binding.checkoutBtn.setOnClickListener(v -> {
            performCheckout();
        });
    }

    private void calculatorCart() {
        double percentTax = 0.02;
        double delivery = 10.0;
        tax = Math.round((managmentCart.getTotalFee() * percentTax * 100)) / 100.0;

        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100 / 100.0);
        double itemTotal = Math.round((managmentCart.getTotalFee() * 100)) / 100.0;

        binding.totalFeeTxt.setText("đ" + itemTotal);
        binding.taxTxt.setText("đ" + tax);
        binding.deliveryTxt.setText("đ" + delivery);
        binding.totalTxt.setText("đ" + total);
    }

    private void performCheckout() {
        String userId = AuthManager.getCurrentUser() != null ? AuthManager.getCurrentUser().getUserId() : null;

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để hoàn tất mua hàng.", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(CartActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        ArrayList<ItemsModel> cartItems = managmentCart.getListCart();
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        double totalAmount = managmentCart.getTotalFee() + tax + 10.0;

        String orderId = mDatabase.child("Orders").push().getKey();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String orderDate = sdf.format(new Date());

        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (ItemsModel item : cartItems) {
            Map<String, Object> itemMap = new HashMap<>();
            // Đã sửa: Xóa dòng itemMap.put("itemId", item.getId()); vì ItemsModel không có trường 'id'.
            // Nếu bạn muốn lưu ID, bạn cần thêm trường 'id' vào ItemsModel và gán giá trị thích hợp.
            itemMap.put("title", item.getTitle());
            itemMap.put("price", item.getPrice());
            itemMap.put("numberInCart", item.getNumberInCart());
            itemMap.put("picUrl", item.getPicUrl());
            orderItems.add(itemMap);
        }

        Map<String, Object> order = new HashMap<>();
        order.put("userId", userId);
        order.put("orderDate", orderDate);
        order.put("totalAmount", totalAmount);
        order.put("items", orderItems);

        if (orderId != null) {
            mDatabase.child("Orders").child(orderId).setValue(order)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(CartActivity.this, "Mua hàng thành công! Đơn hàng #" + orderId, Toast.LENGTH_LONG).show();
                        managmentCart.clearCart();
                        initCartList();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(CartActivity.this, "Lỗi khi mua hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(CartActivity.this, "Không thể tạo ID đơn hàng.", Toast.LENGTH_SHORT).show();
        }
    }
}