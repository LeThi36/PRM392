package com.example.project2272.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.Collections;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider; // Sử dụng ViewModelProvider để khởi tạo ViewModel
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.example.project2272.Activity.CartActivity; // Thêm import này
import com.example.project2272.Adapter.CategoryAdapter;
import com.example.project2272.Adapter.PopularAdapter;
import com.example.project2272.Adapter.SliderAdapter;
import com.example.project2272.Domain.BannerModel;
import com.example.project2272.Model.User;
import com.example.project2272.Domain.CategoryModel; // Đã có
import com.example.project2272.Domain.ItemsModel; // Thêm import này
import com.example.project2272.R;
import com.example.project2272.Repository.UserRepository;
import com.example.project2272.ViewModel.MainViewModel;
import com.example.project2272.databinding.ActivityMainBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.app.Dialog;
import android.view.Window;

import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ArrayList<com.example.project2272.Domain.ItemsModel> originalPopularList = new ArrayList<>();
    private ArrayList<com.example.project2272.Domain.ItemsModel> popularList = new ArrayList<>();
    private com.example.project2272.Adapter.PopularAdapter popularAdapter;
    private String userId;
    private int selectedCategoryId = -1;
    private String selectedCategoryTitle = ""; // Thêm biến này
    private String currentSearchText = ""; // Thêm biến để track search text

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initListeners();
        initCategory();
        initSlider();
        initPopular();
    }

    private void initListeners() {
        // Sự kiện cho nút Giỏ hàng
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));

        // ĐÃ SỬA: Sự kiện cho nút Cài đặt (ID là imageView3 từ layout của bạn)
        // Bây giờ nút có biểu tượng cài đặt sẽ dẫn đến SettingsActivity.
        binding.imageView3.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        // Thiết lập cho Bottom Navigation
        bottomNavigation();

        userId = getIntent().getStringExtra("userId");
        if (userId != null) {
            UserRepository userRepository = new UserRepository();
            userRepository.getUserById(userId, snapshot -> {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getUsername() != null) {
                        binding.textView5.setText(user.getUsername());
                    }
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(MainActivity.this)
                                .load(user.getAvatarUrl())
                                .placeholder(R.drawable.ic_default_avatar)
                                .error(R.drawable.ic_default_avatar)
                                .circleCrop()
                                .into(binding.imageView2); // 👈 Load avatar vào đây
                    }
                }
            });
        }

        // Click avatar hoặc tên để mở ViewProfileActivity
        binding.imageView2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        binding.textView5.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });




        // Search functionality
        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPopular(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // ĐÃ SỬA: Filter/Sort dialog với khoảng giá và 3 tùy chọn sắp xếp
        // Chức năng này vẫn được gán cho imageView5 (biểu tượng lọc).
        binding.imageView5.setOnClickListener(v -> {
            Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bottom_sheet_filter_sort);
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setWindowAnimations(R.style.DialogSlideAnimation);
            }

            // Lấy các view
            EditText etPriceFrom = dialog.findViewById(R.id.etPriceFrom);
            EditText etPriceTo = dialog.findViewById(R.id.etPriceTo);
            RadioButton rbRating = dialog.findViewById(R.id.rbRating);
            RadioButton rbPriceAsc = dialog.findViewById(R.id.rbPriceAsc);
            RadioButton rbPriceDesc = dialog.findViewById(R.id.rbPriceDesc);
            Button btnApply = dialog.findViewById(R.id.btnApply);
            ImageView ivBack = dialog.findViewById(R.id.ivBack);

            if (ivBack != null) {
                ivBack.setOnClickListener(view -> dialog.dismiss());
            }

            if (btnApply != null) {
                btnApply.setOnClickListener(view -> {
                    // Lấy giá trị khoảng giá
                    String priceFromStr = etPriceFrom != null ? etPriceFrom.getText().toString().replaceAll("[^0-9]", "") : "";
                    String priceToStr = etPriceTo != null ? etPriceTo.getText().toString().replaceAll("[^0-9]", "") : "";

                    Double priceFrom = priceFromStr.isEmpty() ? null : Double.parseDouble(priceFromStr);
                    Double priceTo = priceToStr.isEmpty() ? null : Double.parseDouble(priceToStr);

                    // Lọc theo category VÀ khoảng giá
                    ArrayList<com.example.project2272.Domain.ItemsModel> filteredList = new ArrayList<>();
                    for (com.example.project2272.Domain.ItemsModel item : originalPopularList) {
                        boolean matchesCategory = (selectedCategoryId == -1 || item.getCategoryId() == selectedCategoryId);
                        boolean matchesSearch = currentSearchText.isEmpty() ||
                                item.getTitle().toLowerCase().contains(currentSearchText.toLowerCase());
                        boolean matchesPrice = true;
                        if (priceFrom != null && item.getPrice() < priceFrom) matchesPrice = false;
                        if (priceTo != null && item.getPrice() > priceTo) matchesPrice = false;

                        if (matchesCategory && matchesSearch && matchesPrice) {
                            filteredList.add(item);
                        }
                    }

                    // Sau đó sắp xếp
                    if (rbRating != null && rbRating.isChecked()) {
                        Collections.sort(filteredList, (o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));
                    } else if (rbPriceAsc != null && rbPriceAsc.isChecked()) {
                        Collections.sort(filteredList, (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice()));
                    } else if (rbPriceDesc != null && rbPriceDesc.isChecked()) {
                        Collections.sort(filteredList, (o1, o2) -> Double.compare(o2.getPrice(), o1.getPrice()));
                    }

                    popularList.clear();
                    popularList.addAll(filteredList);
                    if (popularAdapter != null) {
                        popularAdapter.updateList(new ArrayList<>(popularList));
                    }
                    dialog.dismiss();
                });
            }

            dialog.show();
        });
    }

    private void bottomNavigation() {
        binding.bottomNavigation.setItemSelected(R.id.home, true);
        binding.bottomNavigation.setOnItemSelectedListener(itemId -> {
            if (itemId == R.id.home) {
                // Ở lại trang chủ
            } else if (itemId == R.id.profile) {
                Intent intent = new Intent(MainActivity.this, ViewProfileActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });


        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
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
                // Navigation logic có thể thêm ở đây
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
        viewModel.loadPopular().observeForever(itemsModels -> {
            if (!itemsModels.isEmpty()) {
                originalPopularList.clear();
                originalPopularList.addAll(itemsModels);
                popularList.clear();
                popularList.addAll(itemsModels);
                if (popularAdapter == null) {
                    popularAdapter = new com.example.project2272.Adapter.PopularAdapter(new ArrayList<>(popularList));
                    binding.popularView.setLayoutManager(
                            new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    binding.popularView.setAdapter(popularAdapter);
                    binding.popularView.setNestedScrollingEnabled(true);
                } else {
                    popularAdapter.updateList(new ArrayList<>(popularList));
                }
            
            // Hiển thị tất cả sản phẩm mặc định
            selectedCategoryId = -1;
            // Không cần gọi filterProductsByCategory() vì đã hiển thị tất cả
        }
        binding.progressBarPopular.setVisibility(View.GONE);
    });
    viewModel.loadPopular();
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
            
            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModels);
            categoryAdapter.setOnCategoryClickListener(new CategoryAdapter.OnCategoryClickListener() {
                @Override
                public void onCategoryClick(int categoryId, String categoryTitle) {
                    Log.d("CategoryClick", "Category clicked: " + categoryTitle + ", ID: " + categoryId);
                    selectedCategoryId = categoryId;
                    // Lưu thông tin category title để xử lý "All Products"
                    selectedCategoryTitle = categoryTitle;
                    filterProductsByCategory();
                }
            });
            
            // Tìm và set "All Products" từ database làm mặc định
            for (int i = 0; i < categoryModels.size(); i++) {
                if ("All Products".equals(categoryModels.get(i).getTitle())) {
                    categoryAdapter.setSelectedPosition(i);
                    selectedCategoryId = categoryModels.get(i).getId();
                    selectedCategoryTitle = "All Products";
                    break;
                }
            }
            
            binding.categoryView.setAdapter(categoryAdapter);
            binding.categoryView.setNestedScrollingEnabled(true);
            binding.progressBarCategory.setVisibility(View.GONE);
            
            // Trigger filter ban đầu
            filterProductsByCategory();
        });
    }

    // Thêm method để filter sản phẩm theo category
    private void filterProductsByCategory() {
        ArrayList<ItemsModel> filteredList = new ArrayList<>();
        
        Log.d("CategoryFilter", "=== FILTER START ===");
        Log.d("CategoryFilter", "Selected Category ID: " + selectedCategoryId);
        Log.d("CategoryFilter", "Selected Category Title: " + selectedCategoryTitle);
        Log.d("CategoryFilter", "Current Search Text: '" + currentSearchText + "'");
        Log.d("CategoryFilter", "Original list size: " + originalPopularList.size());
        
        for (ItemsModel item : originalPopularList) {
            // Sửa logic: nếu là "All Products" thì hiển thị tất cả
            boolean matchesCategory = "All Products".equals(selectedCategoryTitle) || 
                        item.getCategoryId() == selectedCategoryId;
            boolean matchesSearch = currentSearchText.isEmpty() || 
                    item.getTitle().toLowerCase().contains(currentSearchText.toLowerCase());
            
            Log.d("CategoryFilter", "Item: " + item.getTitle() + 
                    ", CategoryId: " + item.getCategoryId() + 
                    ", MatchesCategory: " + matchesCategory + 
                    ", MatchesSearch: " + matchesSearch);
            
            if (matchesCategory && matchesSearch) {
                filteredList.add(item);
            }
        }
        
        Log.d("CategoryFilter", "Filtered list size: " + filteredList.size());
        Log.d("CategoryFilter", "=== FILTER END ===");
        
        popularList.clear();
        popularList.addAll(filteredList);
        if (popularAdapter != null) {
            popularAdapter.updateList(new ArrayList<>(popularList));
        }
    }

    // Cập nhật method filterPopular để tích hợp với category filter
    private void filterPopular(String text) {
        currentSearchText = text;
        filterProductsByCategory();
    }
}