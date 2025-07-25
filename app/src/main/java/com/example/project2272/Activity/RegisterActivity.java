package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton; // Import ImageButton
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2272.Manager.AuthManager;
import com.example.project2272.Model.User;
import com.example.project2272.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etUsername, etPhone, etAvatarUrl;
    private Button btnRegister;
    private ImageButton btnBack; // Khai báo nút quay lại
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etRegEmail);
        etPassword = findViewById(R.id.etRegPassword);
        etUsername = findViewById(R.id.etRegUsername);
        etPhone = findViewById(R.id.etRegPhone);
        etAvatarUrl = findViewById(R.id.etRegAvatarUrl); // Nếu có

        btnRegister = findViewById(R.id.btnRegisterUser);
        btnBack = findViewById(R.id.btnBack); // Ánh xạ nút quay lại

        authManager = new AuthManager();

        // Xử lý sự kiện khi nhấn nút đăng ký
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String avatarUrl = etAvatarUrl.getText().toString().trim(); // Có thể để trống hoặc mặc định

            if (email.isEmpty() || password.isEmpty() || username.isEmpty() || phone.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng nhập đầy đủ thông tin bắt buộc.", Toast.LENGTH_SHORT).show();
                return;
            }

            authManager.register(email, password, username, phone, avatarUrl, new AuthManager.AuthListener() {
                @Override
                public void onSuccess(User user) {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    // Sau khi đăng ký thành công, sẽ chuyển về màn hình chính (hoặc LoginActivity nếu muốn)
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Kết thúc RegisterActivity
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Xử lý sự kiện khi nhấn nút quay lại
        btnBack.setOnClickListener(v -> {
            // Quay trở lại trang LoginActivity
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Kết thúc RegisterActivity
        });
    }

    // Bạn cũng có thể override phương thức onBackPressed để xử lý nút Back vật lý của thiết bị
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}