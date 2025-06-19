package com.example.project2272.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.project2272.Domain.BannerModel;
import com.example.project2272.Domain.CategoryModel;
import com.example.project2272.Repository.MainRepository;

import java.util.ArrayList;

public class MainViewModel extends ViewModel {
    private final MainRepository repository = new MainRepository();

    public LiveData<ArrayList<CategoryModel>> loadCategory() {
        return repository.loadCategory();
    }

    public LiveData<ArrayList<BannerModel>> loadBanner() {
        return repository.loadBanner();
    }
}
