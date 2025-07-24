package com.example.project2272.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2272.Domain.OrderModel;
import com.example.project2272.R;

import java.util.ArrayList;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<OrderModel> orderList;

    public OrderHistoryAdapter(ArrayList<OrderModel> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đã sửa: Sử dụng viewholder_order_history.xml
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_history, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        
        holder.orderIdTxt.setText("Mã đơn: " + order.getOrderId());
        holder.orderDateTxt.setText(order.getOrderDate());
        holder.totalAmountTxt.setText("đ" + String.format("%.0f", order.getTotalAmount()));
        
        // Hiển thị phương thức thanh toán và trạng thái
        String paymentInfo = "";
        if ("COD".equals(order.getPaymentMethod())) {
            paymentInfo = "💰 COD - " + order.getOrderStatus();
        } else if ("VNPay".equals(order.getPaymentMethod())) {
            paymentInfo = "💳 VNPay - " + order.getOrderStatus();
        }
        
        // Thêm TextView để hiển thị payment info nếu chưa có trong layout
        // holder.paymentInfoTxt.setText(paymentInfo);
        
        // Setup RecyclerView cho items
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItemsAdapter itemsAdapter = new OrderItemsAdapter(order.getItems());
            holder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.itemsRecyclerView.setAdapter(itemsAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTxt, orderDateTxt, totalAmountTxt;
        RecyclerView itemsRecyclerView; // RecyclerView cho các mặt hàng trong đơn hàng

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderDateTxt = itemView.findViewById(R.id.orderDateTxt);
            totalAmountTxt = itemView.findViewById(R.id.totalAmountTxt);
            // Đã sửa: Sử dụng ID itemsRecyclerView như trong viewholder_order_history.xml
            itemsRecyclerView = itemView.findViewById(R.id.itemsRecyclerView);
        }
    }
}