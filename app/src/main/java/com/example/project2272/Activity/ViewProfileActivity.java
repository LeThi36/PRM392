package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.project2272.Model.User;
import com.example.project2272.R;
import com.example.project2272.Repository.UserRepository;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ViewProfileActivity extends AppCompatActivity {

    private ImageView imageViewProfilePic;
    private TextView textName, textEmail, textPhone;
    private Button btnUpdate;
    private String userId;


    private final UserRepository userRepository = new UserRepository(); // lấy từ Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        userId = getIntent().getStringExtra("userId");

        imageViewProfilePic = findViewById(R.id.imageViewProfilePic);
        textName = findViewById(R.id.textName);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);
        btnUpdate = findViewById(R.id.btnUpdateProfile);

        userRepository.getUserById(userId, snapshot -> {
            if (snapshot == null || !snapshot.exists()) {
                Toast.makeText(this, "Không tìm thấy user trong DB!", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = snapshot.getValue(User.class);
            if (user == null) {
                Toast.makeText(this, "Dữ liệu người dùng không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tiếp tục xử lý
        });


        if (userId != null) {
            userRepository.getUserById(userId, snapshot -> {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    textName.setText(user.getUsername());
                    textEmail.setText(user.getEmail());
                    textPhone.setText(user.getPhone());

                    // Load avatar bằng Glide nếu có, nếu không thì để mặc định
                    String avatarUrl = user.getAvatarUrl();
                    if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
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
            });
        }

        btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        ChipNavigationBar bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setItemSelected(R.id.profile, true); // Hoặc R.id.home tùy theo bạn đang ở đâu

        bottomNavigation.setOnItemSelectedListener(id -> {
            if (id == R.id.home) {
                Intent intent = new Intent(ViewProfileActivity.this, MainActivity.class);
                intent.putExtra("userId", getIntent().getStringExtra("userId"));
                startActivity(intent);
                finish(); // nếu muốn quay về luôn
            } else if (id == R.id.profile) {
                // Đang ở đây rồi, không cần xử lý
            }
        });

    }

}
