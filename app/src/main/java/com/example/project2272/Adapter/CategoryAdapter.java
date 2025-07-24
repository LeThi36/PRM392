package com.example.project2272.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2272.Domain.CategoryModel;
import com.example.project2272.R;
import com.example.project2272.databinding.ViewholderCategoryBinding;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<CategoryModel> items;
    private Context context;
    private int selectedPosition = 0; // Thay đổi từ -1 thành 0 để chọn "All Categories" mặc định
    private int lastSelectedPosition = -1;
    private OnCategoryClickListener listener;

    // Thêm interface
    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId, String categoryTitle);
    }

    public CategoryAdapter(ArrayList<CategoryModel> items) {
        this.items = items;
    }

    // Thêm method để set listener
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(context)
                ,parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        holder.binding.titleTxt.setText(items.get(position).getTitle());

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                lastSelectedPosition = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(selectedPosition);
                
                // Thêm callback khi click category
                if (listener != null) {
                    CategoryModel selectedCategory = items.get(position);
                    android.util.Log.d("CategoryAdapter", "Category clicked: " + selectedCategory.getTitle() + ", ID: " + selectedCategory.getId());
                    listener.onCategoryClick(selectedCategory.getId(), selectedCategory.getTitle());
                }
            }
        });
        
        if (selectedPosition == position){
            holder.binding.titleTxt.setBackgroundResource(R.drawable.orange_bg);
            holder.binding.titleTxt.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.binding.titleTxt.setBackgroundResource(R.drawable.stroke_bg);
            holder.binding.titleTxt.setTextColor(context.getResources().getColor(R.color.black));
        }
    }
    
    // Thêm method để set selected position từ bên ngoài
    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCategoryBinding binding;
        public ViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
