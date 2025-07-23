package com.example.project2272.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project2272.Adapter.CartAdapter;
import com.example.project2272.Helper.ManagmentCart;
import com.example.project2272.R;
import com.example.project2272.databinding.ActivityCartBinding;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        calculateCart();
        setVariable();
        initCartList();
    }

    private void initCartList() {
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollView2.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollView2.setVisibility(View.VISIBLE);

        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart()));
    }

    private void setVariable(){
        binding.backBtn.setOnClickListener(v -> finish());
    }
    private void calculateCart(){
        double percentTax = 0.02;
        double delivery = 10.0;
        tax= Math.round((managmentCart.getTotalFee() * percentTax)) / 100.0;

        double total =Math.round((managmentCart.getTotalFee() + tax + delivery) / 100.0);
        double itemTotal = Math.round(managmentCart.getTotalFee()) / 100.0;

        binding.totalFeeTxt.setText("đ" + itemTotal);
        binding.taxTxt.setText("đ" + tax);
        binding.deliveryTxt.setText("đ" + delivery);
        binding.totalTxt.setText("đ" + total);
    }
}