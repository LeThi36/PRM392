package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project2272.Manager.AuthManager;
import com.example.project2272.Model.User;
import com.example.project2272.R;
// import com.example.project2272.Utils.SessionManager; // Xóa hoặc comment dòng này

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private AuthManager authManager;
    // private SessionManager sessionManager; // Xóa dòng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- XÓA TOÀN BỘ PHẦN KHỞI TẠO VÀ KIỂM TRA SESSION ---
        // sessionManager = SessionManager.getInstance(getApplicationContext());
        // if (SessionManager.getInstance().isLoggedIn()) {
        //     startActivity(new Intent(LoginActivity.this, MainActivity.class));
        //     finish();
        //     return;
        // }
        // --- KẾT THÚC PHẦN XÓA ---

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        authManager = new AuthManager();

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ email và mật khẩu.", Toast.LENGTH_SHORT).show();
                return;
            }

            authManager.login(email, password, new AuthManager.AuthListener() {
                @Override
                public void onSuccess(User user) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}