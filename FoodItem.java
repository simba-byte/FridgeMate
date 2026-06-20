package com.example.fridgemate.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_items")
public class FoodItem {
    // Room 使用自增主键区分每一条食材记录。
    @PrimaryKey(autoGenerate = true)
    public int id;

    // 食材基础信息：名称、分类、数量、单位和存放区域。
    public String name;
    public String category;
    public int quantity;
    public String unit;
    public String storageLocation;

    // 日期统一使用 yyyy-MM-dd 字符串保存，便于表单录入和列表展示。
    public String purchaseDate;
    public String expireDate;

    // status 由 StatusUtils 根据过期日期计算，也可被用户手动标记为“已使用”。
    public String status;
    public String note;
    public long createdAt;

    public FoodItem(String name, String category, int quantity, String unit, String storageLocation,
                    String purchaseDate, String expireDate, String status, String note, long createdAt) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.storageLocation = storageLocation;
        this.purchaseDate = purchaseDate;
        this.expireDate = expireDate;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
    }
}
