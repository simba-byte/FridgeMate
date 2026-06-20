package com.example.fridgemate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fridgemate.R;
import com.example.fridgemate.activity.AddEditFoodActivity;
import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.entity.Recipe;
import com.example.fridgemate.repository.FoodRepository;
import com.example.fridgemate.repository.RecipeRepository;
import com.example.fridgemate.utils.RecipeMatchUtils;
import com.example.fridgemate.utils.StatusUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private TextView textTotal;
    private TextView textSoon;
    private TextView textExpired;
    private TextView textPriorityFoods;
    private TextView textRecipeName;
    private TextView textRecipeDetail;
    private FoodRepository foodRepository;
    private RecipeRepository recipeRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        textTotal = view.findViewById(R.id.textTotal);
        textSoon = view.findViewById(R.id.textSoon);
        textExpired = view.findViewById(R.id.textExpired);
        textPriorityFoods = view.findViewById(R.id.textPriorityFoods);
        textRecipeName = view.findViewById(R.id.textRecipeName);
        textRecipeDetail = view.findViewById(R.id.textRecipeDetail);
        Button quickAdd = view.findViewById(R.id.buttonQuickAdd);
        quickAdd.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddEditFoodActivity.class)));
        foodRepository = new FoodRepository(requireContext());
        recipeRepository = new RecipeRepository(requireContext());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        // 首页同时依赖食材和菜谱：先读食材统计，再读菜谱生成今日推荐。
        foodRepository.getAll(foods -> recipeRepository.getAll(recipes -> requireActivity().runOnUiThread(() -> show(foods, recipes))));
    }

    private void show(List<FoodItem> foods, List<Recipe> recipes) {
        int soon = 0;
        int expired = 0;
        List<String> priority = new ArrayList<>();
        for (FoodItem item : foods) {
            // 临期食材会出现在首页提醒中，已过期食材只计数提示风险。
            if (StatusUtils.SOON.equals(item.status)) {
                soon++;
                priority.add(item.name + "（" + item.expireDate + "）");
            } else if (StatusUtils.EXPIRED.equals(item.status)) {
                expired++;
            }
        }
        textTotal.setText(String.valueOf(foods.size()));
        textSoon.setText(String.valueOf(soon));
        textExpired.setText(String.valueOf(expired));
        textPriorityFoods.setText(priority.isEmpty() ? "你的冰箱状态良好" : join(priority, "、"));

        List<RecipeMatchUtils.RecipeMatch> matches = RecipeMatchUtils.buildRecommendations(foods, recipes);
        if (matches.isEmpty()) {
            textRecipeName.setText("暂无推荐");
            textRecipeDetail.setText("添加食材后可生成推荐");
        } else {
            // 推荐列表已经按匹配度降序排列，首页直接取第一条作为今日推荐。
            RecipeMatchUtils.RecipeMatch best = matches.get(0);
            textRecipeName.setText(best.recipe.recipeName);
            textRecipeDetail.setText("匹配度 " + best.score + "% | " + best.actionText());
        }
    }

    private String join(List<String> values, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(separator);
            }
            builder.append(values.get(i));
        }
        return builder.toString();
    }
}
