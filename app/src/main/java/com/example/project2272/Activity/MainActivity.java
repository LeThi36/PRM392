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

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.app.Dialog;
import android.view.Window;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ArrayList<com.example.project2272.Domain.ItemsModel> originalPopularList = new ArrayList<>();
    private ArrayList<com.example.project2272.Domain.ItemsModel> popularList = new ArrayList<>();
    private com.example.project2272.Adapter.PopularAdapter popularAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new MainViewModel();

        initCategory();
        initSlider();
        initPopular();
        bottomNavigation();

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

        // Filter/Sort dialog với khoảng giá và 3 tùy chọn sắp xếp
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
                    
                    // Lọc theo khoảng giá trước
                    ArrayList<com.example.project2272.Domain.ItemsModel> filteredList = new ArrayList<>();
                    for (com.example.project2272.Domain.ItemsModel item : originalPopularList) {
                        boolean match = true;
                        if (priceFrom != null && item.getPrice() < priceFrom) match = false;
                        if (priceTo != null && item.getPrice() > priceTo) match = false;
                        if (match) filteredList.add(item);
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
        binding.bottomNavigation.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                // Navigation logic có thể thêm ở đây
            }
        });
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
    }

    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
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
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
        viewModel.loadPopular();
    }

    private void filterPopular(String text) {
        ArrayList<com.example.project2272.Domain.ItemsModel> filteredList = new ArrayList<>();
        for (com.example.project2272.Domain.ItemsModel item : originalPopularList) {
            if (item.getTitle() != null && item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        popularList.clear();
        popularList.addAll(filteredList);
        if (popularAdapter != null) {
            popularAdapter.updateList(new ArrayList<>(popularList));
        }
    }

    private void initSlider() {
        binding.progressBarSlider.setVisibility(View.VISIBLE);
        viewModel.loadBanner().observeForever(bannerModels -> {
            if (bannerModels != null && !bannerModels.isEmpty()) {
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