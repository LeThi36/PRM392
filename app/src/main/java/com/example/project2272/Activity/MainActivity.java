package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.project2272.Adapter.CategoryAdapter;
import com.example.project2272.Adapter.PopularAdapter;
import com.example.project2272.Adapter.SliderAdapter;
import com.example.project2272.Domain.BannerModel;
import com.example.project2272.R;
// import com.example.project2272.Utils.SessionManager; // Xóa hoặc comment dòng này
import com.example.project2272.ViewModel.MainViewModel;
import com.example.project2272.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    // private SessionManager sessionManager; // Xóa dòng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- XÓA TOÀN BỘ PHẦN KHỞI TẠO VÀ KIỂM TRA SESSION ---
        // sessionManager = SessionManager.getInstance();
        // if (!sessionManager.isLoggedIn()) {
        //     Log.d("MainActivity", "Người dùng chưa đăng nhập. Chuyển hướng tới LoginActivity.");
        //     Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        //     startActivity(intent);
        //     finish();
        //     return;
        // }
        // (Tùy chọn) Log thông tin người dùng đã đăng nhập để kiểm tra
        // Log.d("MainActivity", "Người dùng đã đăng nhập. ID: " + sessionManager.getUserId() +
        //         ", Email: " + sessionManager.getEmail() +
        //         ", Tên: " + sessionManager.getUsername());
        // --- KẾT THÚC PHẦN XÓA ---

        viewModel = new MainViewModel();

        initCategory();
        initSlider();
        initPopular();
        bottomNavigation();
    }

    private void bottomNavigation(){
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                // Bạn có thể thêm logic điều hướng cho các mục bottom navigation ở đây.
                // Ví dụ:
                // if (i == R.id.profile_menu_item) {
                //     startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                // } else if (i == R.id.logout_menu_item) {
                //     // Thực hiện đăng xuất (nếu bạn có một AuthManager riêng biệt cho việc này)
                //     // new AuthManager().logout();
                //     // startActivity(new Intent(MainActivity.this, LoginActivity.class));
                //     // finish();
                // }
            }
        });
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        viewModel.loadPopular().observeForever(itemsModels -> {
            if(!itemsModels.isEmpty()){
                binding.popularView.setLayoutManager(
                        new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                binding.popularView.setAdapter(new PopularAdapter(itemsModels));
                binding.popularView.setNestedScrollingEnabled(true);
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
        viewModel.loadPopular();
    }

    private void initSlider() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observeForever(bannerModels -> {
            if(bannerModels != null && !bannerModels.isEmpty()){
                banners(bannerModels);
                binding.progressBarSlider.setVisibility(View.GONE);
            }
        });
        viewModel.loadBanner();
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
        viewModel.loadCategory().observeForever(categoryModels -> {
            binding.categoryView.setLayoutManager(new LinearLayoutManager(
                    MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
            binding.categoryView.setAdapter(new CategoryAdapter(categoryModels));
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE);
        });
        viewModel.loadCategory();
    }
}