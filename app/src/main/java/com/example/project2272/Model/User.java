package com.example.project2272.Model;

public class User {
    private String userId;
    private String email;
    private String password; // Đã được hash
    private String username;
    private String phone;
    private String avatarUrl;

    public User() {
        // Constructor rỗng cần thiết cho Firebase
    }

    public User(String userId, String email, String password, String username, String phone, String avatarUrl) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }

    // --- Getters và Setters ---
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}
