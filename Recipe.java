package com.example.fridgemate.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // 菜谱材料使用字符串保存，推荐时再按顿号/逗号拆分，降低表结构复杂度。
    public String recipeName;
    public String materials;
    public String steps;
    public String difficulty;
    public int cookingTime;

    public Recipe(String recipeName, String materials, String steps, String difficulty, int cookingTime) {
        this.recipeName = recipeName;
        this.materials = materials;
        this.steps = steps;
        this.difficulty = difficulty;
        this.cookingTime = cookingTime;
    }
}
