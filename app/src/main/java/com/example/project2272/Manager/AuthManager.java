package com.example.project2272.Manager;

import com.example.project2272.Model.User;
import com.example.project2272.Repository.UserRepository;
import com.example.project2272.Utils.PasswordHasher;
// import com.example.project2272.Utils.SessionManager; // Xóa hoặc comment dòng này
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthManager {

    private UserRepository userRepository;
    // private SessionManager sessionManager; // Xóa dòng này

    public AuthManager() {
        userRepository = new UserRepository();
        // sessionManager = SessionManager.getInstance(); // Xóa dòng này
    }

    public interface AuthListener {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public void register(String email, String password, String username, String phone, String avatarUrl, AuthListener listener) {
        // Kiểm tra email đã tồn tại
        userRepository.getUserByEmail(email, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listener.onFailure("Email này đã được đăng ký.");
                } else {
                    String userId = UUID.randomUUID().toString();
                    String hashedPassword = PasswordHasher.hashPassword(password);

                    if (hashedPassword == null) {
                        listener.onFailure("Lỗi khi xử lý mật khẩu.");
                        return;
                    }

                    User newUser = new User(userId, email, hashedPassword, username, phone, avatarUrl);
                    userRepository.addUser(newUser, task -> {
                        if (task.isSuccessful()) {
                            // --- XÓA DÒNG LƯU SESSION SAU KHI ĐĂNG KÝ ---
                            // sessionManager.saveLoginState(userId, email, username, phone, avatarUrl);
                            listener.onSuccess(newUser);
                        } else {
                            listener.onFailure("Đăng ký thất bại: " + task.getException().getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public void login(String email, String password, AuthListener listener) {
        userRepository.getUserByEmail(email, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && PasswordHasher.verifyPassword(password, user.getPassword())) {
                            // --- XÓA DÒNG LƯU SESSION SAU KHI ĐĂNG NHẬP ---
                            // sessionManager.saveLoginState(user.getUserId(), user.getEmail(), user.getUsername(), user.getPhone(), user.getAvatarUrl());
                            listener.onSuccess(user);
                            return;
                        }
                    }
                    listener.onFailure("Email hoặc mật khẩu không đúng.");
                } else {
                    listener.onFailure("Email hoặc mật khẩu không đúng.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFailure(error.getMessage());
            }
        });
    }

    public void updateProfile(String userId, String username, String phone, String avatarUrl, AuthListener listener) {
        Map<String, Object> updates = new HashMap<>();
        if (username != null && !username.isEmpty()) updates.put("username", username);
        if (phone != null && !phone.isEmpty()) updates.put("phone", phone);
        if (avatarUrl != null && !avatarUrl.isEmpty()) updates.put("avatarUrl", avatarUrl);

        userRepository.updateProfile(userId, updates, task -> {
            if (task.isSuccessful()) {
                // --- XÓA DÒNG CẬP NHẬT SESSION PROFILE ---
                // sessionManager.updateSessionProfile(username, phone, avatarUrl);
                // Lấy lại thông tin user từ DB để đảm bảo thông tin trả về là mới nhất
                userRepository.getUserById(userId, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User updatedUser = snapshot.getValue(User.class);
                        if (updatedUser != null) {
                            listener.onSuccess(updatedUser);
                        } else {
                            listener.onFailure("Không tìm thấy thông tin người dùng sau khi cập nhật.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onFailure(error.getMessage());
                    }
                });
            } else {
                listener.onFailure("Cập nhật profile thất bại: " + task.getException().getMessage());
            }
        });
    }

    public void logout() {
        // --- XÓA DÒNG LOGOUT SESSION ---
        // sessionManager.logout();
    }
}