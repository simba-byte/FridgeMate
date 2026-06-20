package com.example.fridgemate.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fridgemate.entity.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    // 菜谱为本地预置数据，按名称排序后展示更稳定。
    @Query("SELECT * FROM recipes ORDER BY recipeName ASC")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    Recipe getById(int id);

    @Query("SELECT COUNT(*) FROM recipes")
    int count();

    // 首次启动时由 AppDatabase 插入默认菜谱。
    @Insert
    long insert(Recipe recipe);
}
