package com.example.fridgemate.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fridgemate.entity.FoodItem;

import java.util.List;

@Dao
public interface FoodDao {
    // 按过期日期升序排列，方便用户优先看到临期食材。
    @Query("SELECT * FROM food_items ORDER BY expireDate ASC, createdAt DESC")
    List<FoodItem> getAll();

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    FoodItem getById(int id);

    @Query("SELECT COUNT(*) FROM food_items")
    int count();

    // 增删改查均通过 DAO 完成，Activity/Fragment 不直接操作数据库。
    @Insert
    long insert(FoodItem item);

    @Update
    void update(FoodItem item);

    @Delete
    void delete(FoodItem item);
}
