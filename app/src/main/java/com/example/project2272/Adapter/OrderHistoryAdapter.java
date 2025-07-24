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
        holder.orderIdTxt.setText("Mã đơn hàng: #" + order.getOrderId());
        holder.orderDateTxt.setText("Ngày đặt: " + order.getOrderDate());
        holder.totalAmountTxt.setText(String.format(Locale.getDefault(), "Tổng tiền: đ%.2f", order.getTotalAmount()));

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            holder.itemsRecyclerView.setVisibility(View.VISIBLE);
            // Truyền trực tiếp List<Map<String, Object>> vào OrderItemsAdapter
            OrderItemsAdapter itemsAdapter = new OrderItemsAdapter(order.getItems());
            holder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            holder.itemsRecyclerView.setAdapter(itemsAdapter);
        } else {
            holder.itemsRecyclerView.setVisibility(View.GONE);
            // Có thể hiển thị một TextView "Không có sản phẩm" nếu bạn muốn
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