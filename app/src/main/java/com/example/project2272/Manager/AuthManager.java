package com.example.project2272.Manager;

import com.example.project2272.Model.User;
import com.example.project2272.Repository.UserRepository;
import com.example.project2272.Utils.PasswordHasher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthManager {

    private UserRepository userRepository;
    private static User currentUser; // Thêm biến tĩnh để lưu người dùng hiện tại

    public AuthManager() {
        userRepository = new UserRepository();
    }

    // Phương thức để lấy người dùng hiện tại
    public static User getCurrentUser() {
        return currentUser;
    }

    public interface AuthListener {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public void register(String email, String password, String username, String phone, String avatarUrl, AuthListener listener) {
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
                            currentUser = newUser; // Đặt người dùng hiện tại sau khi đăng ký thành công
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
                            currentUser = user; // Lưu thông tin người dùng sau khi đăng nhập thành công
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
                userRepository.getUserById(userId, new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User updatedUser = snapshot.getValue(User.class);
                        if (updatedUser != null) {
                            if (currentUser != null && currentUser.getUserId().equals(updatedUser.getUserId())) {
                                currentUser = updatedUser; // Cập nhật người dùng hiện tại trong AuthManager
                            }
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

    public static void logout() { // Thay đổi thành static
        currentUser = null; // Xóa thông tin người dùng hiện tại khi đăng xuất
    }
}