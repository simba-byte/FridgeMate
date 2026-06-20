package com.example.fridgemate.repository;

import android.content.Context;

import com.example.fridgemate.database.AppDatabase;
import com.example.fridgemate.database.RecipeDao;
import com.example.fridgemate.entity.Recipe;

import java.util.List;

public class RecipeRepository {
    private final RecipeDao recipeDao;

    public RecipeRepository(Context context) {
        recipeDao = AppDatabase.getDatabase(context).recipeDao();
    }

    public void getAll(DataCallback<List<Recipe>> callback) {
        // 菜谱数据量较小，但仍保持后台线程读取，和其他 Repository 风格一致。
        AppDatabase.databaseExecutor.execute(() -> callback.onData(recipeDao.getAll()));
    }

    public void getById(int id, DataCallback<Recipe> callback) {
        AppDatabase.databaseExecutor.execute(() -> callback.onData(recipeDao.getById(id)));
    }
}
