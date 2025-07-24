package com.example.project2272.Repository;

import androidx.annotation.NonNull;

import com.example.project2272.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.function.Consumer;

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

    public void getUserById(String userId, Consumer<DataSnapshot> listener) {
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listener.accept(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Bạn có thể log hoặc xử lý nếu cần
            }
        });
    }

    public void updateProfile(String userId, Map<String, Object> updates, OnCompleteListener<Void> listener) {
        usersRef.child(userId).updateChildren(updates).addOnCompleteListener(listener);
    }
}
