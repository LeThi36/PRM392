package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Sử dụng ViewModelProvider để khởi tạo ViewModel
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.project2272.Adapter.CategoryAdapter;
import com.example.project2272.Adapter.PopularAdapter;
import com.example.project2272.Adapter.SliderAdapter;
import com.example.project2272.Domain.BannerModel;
import com.example.project2272.R;
import com.example.project2272.ViewModel.MainViewModel;
import com.example.project2272.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Không cần EdgeToEdge.enable(this) nếu bạn không tùy chỉnh sâu về nó
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo ViewModel theo cách chuẩn
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initListeners(); // (THAY ĐỔI 1) - Gọi hàm khởi tạo các listener
        initCategory();
        initSlider();
        initPopular();
    }

    // (THAY ĐỔI 2) - Nhóm tất cả các listener vào một hàm riêng để code sạch sẽ hơn
    private void initListeners() {
        // Sự kiện cho nút Giỏ hàng
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));

        // Sự kiện cho nút Cài đặt (ID là imageView5 từ layout của bạn)
        binding.imageView5.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        // Thiết lập cho Bottom Navigation
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if (i == R.id.home) {
                    // Đang ở trang Home rồi, không cần làm gì
                }
                // Bạn có thể thêm logic điều hướng cho các mục khác ở đây
                // Ví dụ:
                // else if (i == R.id.profile) {
                //     startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                // }
            }
        });
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        // (TỐI ƯU 1) - Dùng observe(this, ...) thay vì observeForever để tự động hủy lắng nghe, tránh memory leak.
        viewModel.loadPopular().observe(this, itemsModels -> {
            if (itemsModels != null && !itemsModels.isEmpty()) {
                binding.popularView.setLayoutManager(
                        new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.popularView.setAdapter(new PopularAdapter(itemsModels));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
        // (TỐI ƯU 2) - Bỏ lệnh gọi `viewModel.loadPopular()` thừa ở đây. `observe` đã đủ để kích hoạt.
    }

    private void initSlider() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observe(this, bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
                banners(bannerModels);
            }
            binding.progressBarSlider.setVisibility(View.GONE);
        });
    }

    private void banners(ArrayList<BannerModel> bannerModels) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(bannerModels, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewPagerSlider.setPageTransformer(compositePageTransformer);
    }

    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        viewModel.loadCategory().observe(this, categoryModels -> {
            if (categoryModels != null && !categoryModels.isEmpty()) {
                binding.categoryView.setLayoutManager(new LinearLayoutManager(
                        MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
                binding.categoryView.setNestedScrollingEnabled(true);
            }
            binding.progressBarCategory.setVisibility(View.GONE);
        });
    }
}