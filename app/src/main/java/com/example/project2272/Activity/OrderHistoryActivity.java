package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2272.Adapter.OrderHistoryAdapter;
import com.example.project2272.Domain.OrderModel;
import com.example.project2272.Manager.AuthManager; // Import AuthManager
import com.example.project2272.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView orderHistoryRecyclerView;
    private OrderHistoryAdapter orderHistoryAdapter;
    private ArrayList<OrderModel> orderList;
    // private FirebaseAuth mAuth; // Xóa hoặc comment dòng này
    private DatabaseReference mDatabase;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // mAuth = FirebaseAuth.getInstance(); // Xóa hoặc comment dòng này
        mDatabase = FirebaseDatabase.getInstance().getReference();

        orderHistoryRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        backBtn = findViewById(R.id.backBtn);

        orderList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(orderList);
        orderHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderHistoryRecyclerView.setAdapter(orderHistoryAdapter);

        backBtn.setOnClickListener(v -> finish());

        loadOrderHistory();
    }

    private void loadOrderHistory() {
        // Lấy userId từ AuthManager thay vì FirebaseAuth
        String userId = AuthManager.getCurrentUser() != null ? AuthManager.getCurrentUser().getUserId() : null;

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem lịch sử mua hàng.", Toast.LENGTH_SHORT).show();
            // Điều hướng đến LoginActivity nếu cần
            Intent loginIntent = new Intent(OrderHistoryActivity.this, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        Query query = mDatabase.child("Orders").orderByChild("userId").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                orderList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        OrderModel order = snapshot.getValue(OrderModel.class);
                        if (order != null) {
                            order.setOrderId(snapshot.getKey());
                            orderList.add(order);
                        }
                    }
                    Collections.sort(orderList, (o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "Không có đơn hàng nào.", Toast.LENGTH_SHORT).show();
                }
                orderHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderHistoryActivity.this, "Lỗi tải lịch sử: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}