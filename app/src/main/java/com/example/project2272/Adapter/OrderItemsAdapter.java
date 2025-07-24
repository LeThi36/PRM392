package com.example.project2272.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project2272.R;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    private List<Map<String, Object>> itemList;

    public OrderItemsAdapter(List<Map<String, Object>> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Đảm bảo bạn có file layout này trong res/layout
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_order_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = itemList.get(position);

        // --- Xử lý Title an toàn hơn ---
        String title = "N/A"; // Giá trị mặc định nếu không tìm thấy title
        Object titleObj = item.get("title");
        if (titleObj instanceof String) {
            title = (String) titleObj;
        }
        holder.titleTxt.setText(title);

        // --- Xử lý Quantity an toàn hơn (có thể là Long hoặc Integer từ Firebase) ---
        int quantity = 0;
        Object numberInCartObj = item.get("numberInCart");
        if (numberInCartObj instanceof Long) {
            quantity = ((Long) numberInCartObj).intValue();
        } else if (numberInCartObj instanceof Integer) {
            quantity = (Integer) numberInCartObj;
        }
        holder.quantityTxt.setText("Số lượng: " + quantity);

        // --- Xử lý Price an toàn hơn (có thể là Double, Long, hoặc Integer từ Firebase) ---
        double price = 0.0;
        Object priceObj = item.get("price");
        if (priceObj instanceof Double) {
            price = (Double) priceObj;
        } else if (priceObj instanceof Long) {
            price = ((Long) priceObj).doubleValue();
        } else if (priceObj instanceof Integer) {
            price = ((Integer) priceObj).doubleValue();
        }
        holder.priceTxt.setText(String.format(Locale.getDefault(), "%.2f VND", price));

        // --- Xử lý PicUrl (đã khá an toàn) ---
        Object picUrlObj = item.get("picUrl");
        if (picUrlObj instanceof List && !((List<?>) picUrlObj).isEmpty()) {
            List<String> picUrls = (List<String>) picUrlObj;
            if (!picUrls.isEmpty()) { // Kiểm tra lại danh sách không rỗng
                Glide.with(holder.itemView.getContext())
                        .load(picUrls.get(0))
                        .into(holder.pic);
            } else {
                holder.pic.setImageResource(R.drawable.ic_launcher_background); // Ảnh mặc định nếu list rỗng
            }
        } else if (picUrlObj instanceof String && !((String) picUrlObj).isEmpty()){
            Glide.with(holder.itemView.getContext())
                    .load((String) picUrlObj)
                    .into(holder.pic);
        }
        else {
            holder.pic.setImageResource(R.drawable.ic_launcher_background); // Ảnh mặc định nếu không có URL
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, quantityTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.itemTitleTxt);
            priceTxt = itemView.findViewById(R.id.itemPriceTxt);
            quantityTxt = itemView.findViewById(R.id.itemQuantityTxt);
            pic = itemView.findViewById(R.id.itemPic);
        }
    }
}