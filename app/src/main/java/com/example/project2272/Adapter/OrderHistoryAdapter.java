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
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_history, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        holder.orderIdTxt.setText("Mã đơn: " + order.getOrderId());
        holder.orderDateTxt.setText(order.getOrderDate());
        holder.totalAmountTxt.setText("Tổng tiền: " + String.format("%.0f", order.getTotalAmount()) + " " + "VND"); // Đã thêm "Tổng tiền: "

        // ĐÃ SỬA: Hiển thị phương thức thanh toán và trạng thái
        String paymentInfo = "";
        if ("COD".equals(order.getPaymentMethod())) {
            paymentInfo = "Phương thức: COD - " + order.getOrderStatus();
        } else if ("VNPay".equals(order.getPaymentMethod())) {
            paymentInfo = "Phương thức: VNPay - " + order.getOrderStatus();
        }
        holder.paymentStatusTxt.setText(paymentInfo); // Gán text cho TextView mới

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
        TextView orderIdTxt, orderDateTxt, totalAmountTxt, paymentStatusTxt; // ĐÃ SỬA: Thêm paymentStatusTxt
        RecyclerView itemsRecyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTxt = itemView.findViewById(R.id.orderIdTxt);
            orderDateTxt = itemView.findViewById(R.id.orderDateTxt);
            totalAmountTxt = itemView.findViewById(R.id.totalAmountTxt);
            paymentStatusTxt = itemView.findViewById(R.id.paymentStatusTxt); // ĐÃ THÊM
            itemsRecyclerView = itemView.findViewById(R.id.itemsRecyclerView);
        }
    }
}