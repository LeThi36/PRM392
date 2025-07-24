package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2272.Manager.AuthManager; // Import AuthManager
import com.example.project2272.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backBtn;
    private LinearLayout profileBtn, logoutBtn, orderHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backBtn = findViewById(R.id.backBtn);
        profileBtn = findViewById(R.id.profileBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        // Đã sửa: Sử dụng R.id.orderHistoryBtn
        orderHistoryBtn = findViewById(R.id.orderHistoryBtn);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        profileBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Chuyển đến trang cập nhật hồ sơ", Toast.LENGTH_SHORT).show();
            // Điều hướng đến ProfileActivity nếu bạn có
            // Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            // startActivity(intent);
        });

        orderHistoryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            AuthManager.logout(); // Sử dụng phương thức logout từ AuthManager
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }
}