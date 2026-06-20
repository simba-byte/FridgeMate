package com.example.fridgemate.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_items")
public class ShoppingItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // 购物清单项只保留采购需要的核心字段，避免和 FoodItem 强绑定。
    public String name;
    public String category;
    public int quantity;
    public String unit;

    // 用于控制列表中“已购买”勾选状态和置灰样式。
    public boolean isBought;
    public long createdAt;

    public ShoppingItem(String name, String category, int quantity, String unit, boolean isBought, long createdAt) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.isBought = isBought;
        this.createdAt = createdAt;
    }
}
