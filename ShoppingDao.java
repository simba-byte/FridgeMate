package com.example.fridgemate.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fridgemate.entity.ShoppingItem;

import java.util.List;

@Dao
public interface ShoppingDao {
    // 未购买项排在前面，已购买项排在后面，符合购物清单使用习惯。
    @Query("SELECT * FROM shopping_items ORDER BY isBought ASC, createdAt DESC")
    List<ShoppingItem> getAll();

    @Query("SELECT * FROM shopping_items WHERE id = :id LIMIT 1")
    ShoppingItem getById(int id);

    @Insert
    long insert(ShoppingItem item);

    @Update
    void update(ShoppingItem item);

    @Delete
    void delete(ShoppingItem item);
}
