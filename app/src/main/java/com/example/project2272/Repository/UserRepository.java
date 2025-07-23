package com.example.project2272.Repository;

import com.example.project2272.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserRepository {

    private DatabaseReference usersRef;

    public UserRepository() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users"); // Node gốc cho người dùng
    }

    public void addUser(User user, OnCompleteListener<Void> listener) {
        usersRef.child(user.getUserId()).setValue(user).addOnCompleteListener(listener);
    }

    public void getUserByEmail(String email, ValueEventListener listener) {
        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(listener);
    }

    public void getUserById(String userId, ValueEventListener listener) {
        usersRef.child(userId).addListenerForSingleValueEvent(listener);
    }

    public void updateProfile(String userId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        usersRef.child(userId).updateChildren(updates).addOnCompleteListener(listener);
    }
}
