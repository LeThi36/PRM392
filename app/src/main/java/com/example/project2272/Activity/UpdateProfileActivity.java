package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project2272.Manager.AuthManager;
import com.example.project2272.Model.User;
import com.example.project2272.R;
import com.example.project2272.Repository.UserRepository;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class UpdateProfileActivity extends AppCompatActivity {

    private ImageView imageViewProfilePic;
    private EditText editFullName, editEmail, editPhone, editAvatarUrl, editPassword;
    private Button btnSaveProfile;

    private AuthManager authManager;
    private UserRepository userRepository;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        userId = getIntent().getStringExtra("userId");

        // Ánh xạ view
        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        editFullName = findViewById(R.id.editFullName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editAvatarUrl = findViewById(R.id.editAvatarUrl);
        editPassword = findViewById(R.id.editPassword);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Khởi tạo
        authManager = new AuthManager();
        userRepository = new UserRepository();


        // Lấy thông tin người dùng
        if (userId != null) {
            userRepository.getUserById(userId, snapshot -> {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    editFullName.setText(user.getUsername());
                    editEmail.setText(user.getEmail());
                    editPhone.setText(user.getPhone());
                    editAvatarUrl.setText(user.getAvatarUrl());
                    // Không set password lên EditText vì bảo mật

                    loadAvatar(user.getAvatarUrl());
                }
            });
        }

        // Cập nhật ảnh khi URL thay đổi
        editAvatarUrl.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String url = editAvatarUrl.getText().toString().trim();
                loadAvatar(url);
            }
        });

        // Sự kiện lưu cập nhật
        btnSaveProfile.setOnClickListener(v -> {
            String newName = editFullName.getText().toString().trim();
            String newPhone = editPhone.getText().toString().trim();
            String newAvatarUrl = editAvatarUrl.getText().toString().trim();
            String newPassword = editPassword.getText().toString().trim();

            if (newName.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tên và số điện thoại.", Toast.LENGTH_SHORT).show();
                return;
            }

            authManager.updateProfile(userId, newName, newPhone, newAvatarUrl, newPassword, new AuthManager.AuthListener() {
                @Override
                public void onSuccess(User updatedUser) {
                    Toast.makeText(UpdateProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(UpdateProfileActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        });

        ChipNavigationBar bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setItemSelected(R.id.profile, true); // Hoặc R.id.home tùy theo bạn đang ở đâu

        bottomNavigation.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent intent = new Intent(UpdateProfileActivity.this, MainActivity.class);
                intent.putExtra("userId", getIntent().getStringExtra("userId"));
                startActivity(intent);
                finish(); // nếu muốn quay về luôn
            } else if (id == R.id.profile) {
                // Đang ở đây rồi, không cần xử lý
            }
        });

    }

    private void loadAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_default_avatar)
                    .error(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(imageViewProfilePic);
        } else {
            imageViewProfilePic.setImageResource(R.drawable.ic_default_avatar);
        }
    }
}
