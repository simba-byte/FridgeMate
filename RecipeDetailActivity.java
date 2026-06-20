package com.example.fridgemate.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fridgemate.R;
import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.entity.Recipe;
import com.example.fridgemate.entity.ShoppingItem;
import com.example.fridgemate.repository.FoodRepository;
import com.example.fridgemate.repository.RecipeRepository;
import com.example.fridgemate.repository.ShoppingRepository;
import com.example.fridgemate.utils.RecipeMatchUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailActivity extends AppCompatActivity {
    public static final String EXTRA_RECIPE_ID = "recipe_id";
    private Recipe recipe;
    private List<String> missingMaterials = new ArrayList<>();
    private ShoppingRepository shoppingRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        shoppingRepository = new ShoppingRepository(this);

        int recipeId = getIntent().getIntExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId <= 0) {
            finish();
            return;
        }
        load(recipeId);

        Button addMissing = findViewById(R.id.buttonAddMissing);
        addMissing.setOnClickListener(v -> addMissingToShoppingList());
    }

    private void load(int recipeId) {
        RecipeRepository recipeRepository = new RecipeRepository(this);
        FoodRepository foodRepository = new FoodRepository(this);
        // 详情页需要同时拿到菜谱和当前食材，才能重新计算缺少材料。
        recipeRepository.getById(recipeId, recipeData -> {
            recipe = recipeData;
            foodRepository.getAll(foodData -> runOnUiThread(() -> show(recipeData, foodData)));
        });
    }

    private void show(Recipe recipeData, List<FoodItem> foodData) {
        if (recipeData == null) {
            Toast.makeText(this, "菜谱不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        List<Recipe> single = new ArrayList<>();
        single.add(recipeData);
        // 复用推荐工具类计算单个菜谱的匹配度，避免详情页另写一套逻辑。
        RecipeMatchUtils.RecipeMatch match = RecipeMatchUtils.buildRecommendations(foodData, single).get(0);
        missingMaterials = match.missingMaterials;

        ((TextView) findViewById(R.id.textRecipeTitle)).setText(recipeData.recipeName);
        ((TextView) findViewById(R.id.textRecipeMeta)).setText(recipeData.difficulty + " | 约 " + recipeData.cookingTime + " 分钟");
        ((TextView) findViewById(R.id.textMaterials)).setText("所需食材：" + recipeData.materials);
        ((TextView) findViewById(R.id.textMatch)).setText("当前匹配度：" + match.score + "%  " + match.actionText());
        ((TextView) findViewById(R.id.textSteps)).setText(recipeData.steps);
        Button addMissing = findViewById(R.id.buttonAddMissing);
        addMissing.setVisibility(missingMaterials.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void addMissingToShoppingList() {
        if (recipe == null || missingMaterials.isEmpty()) {
            Toast.makeText(this, "当前菜谱无需补充食材", Toast.LENGTH_SHORT).show();
            return;
        }
        // 将缺少材料逐个加入购物清单，便于用户后续采购。
        for (String material : missingMaterials) {
            ShoppingItem item = new ShoppingItem(material, "其他", 1, "份", false, System.currentTimeMillis());
            shoppingRepository.insert(item, null);
        }
        Toast.makeText(this, "已加入购物清单", Toast.LENGTH_SHORT).show();
    }
}
